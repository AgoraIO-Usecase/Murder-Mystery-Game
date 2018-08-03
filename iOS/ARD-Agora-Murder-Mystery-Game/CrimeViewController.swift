//
//  CrimeViewController.swift
//  ARD-Agora-Murder-Mystery-Game
//
//  Created by CavanSu on 2018/7/26.
//  Copyright © 2018 Agora. All rights reserved.
//

import UIKit
import AgoraAudioKit

class CrimeViewController: UIViewController {
    @IBOutlet weak var voicePlayButton: UIButton!
    @IBOutlet weak var voiceRecordButton: UIButton!
    @IBOutlet weak var bgImageView: UIImageView!
    @IBOutlet weak var usersCollectionView: UICollectionView!
    @IBOutlet var buttons: [UIButton]!
    
    var userList = [UserInfo]() {
        didSet {
            usersCollectionView?.reloadData()
        }
    }
    
    var agoraKit: AgoraRtcEngineKit!
    var agoraStatus = AgoraStatus.sharedStatus()
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
  
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(true)
        voicePlayButton.isSelected = agoraStatus.muteAllRemote
        voiceRecordButton.isSelected = agoraStatus.muteLocalAudio
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        updateViews()
        loadAgoraKit()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        guard let identifier = segue.identifier else {
            return
        }
        // 监听是进入走廊或者卧室
        let vc = segue.destination as! SceneViewController
        vc.agoraKit = agoraKit
        
        switch identifier {
        case "ToHall" :
            vc.scene = .hall
        case "ToBedroom" :
            vc.scene = .bedroom
        default: break
        }
    }
    
    @IBAction func doVoicePlayPressed(_ sender: UIButton) {
        sender.isSelected = !sender.isSelected
        // 开始或者停止播放音频流
        agoraKit.muteAllRemoteAudioStreams(sender.isSelected)
        agoraStatus.muteAllRemote = sender.isSelected
    }
    
    @IBAction func doVoiceRecordPressed(_ sender: UIButton) {
        sender.isSelected = !sender.isSelected
        // 开始或者停止发送音频流
        agoraKit.muteLocalAudioStream(sender.isSelected)
        agoraStatus.muteLocalAudio = sender.isSelected
    }
    
    @IBAction func doBackButtonPressed(_ sender: UIButton) {
        // 退出当前剧本，离开群聊频道
        agoraKit.leaveChannel(nil)
        agoraStatus.muteAllRemote = false
        agoraStatus.muteLocalAudio = false
        self.navigationController?.popViewController(animated: true)
    }
    
    @IBAction func doExitPressed(_ sender: UIStoryboardSegue) {
        // 监听pop返回的来源对象，如果是PrivateChatViewController的对象就重新加入群聊频道
        if sender.source is PrivateChatViewController {
            // 将agoraStatus的delegate重新至回CrimeViewController，使CrimeViewController能够收到AgoraRtcEngineDelegate回调
            agoraKit.delegate = self
            
            // 通信模式下默认为听筒，demo中将它切为外放
            agoraKit.setDefaultAudioRouteToSpeakerphone(true)
            
            // 从私聊返回案发现场时，重新加入案发现场的群聊频道
            agoraKit.joinChannel(byToken: nil, channelId: KeyCenter.crimeChannelId(), info: nil, uid: 0, joinSuccess: nil)
        }
    }
}

// MARK: AgoraRtcEngineKit
private extension CrimeViewController {
    func loadAgoraKit() {
        // 初始化AgoraRtcEngineKit
        agoraKit = AgoraRtcEngineKit.sharedEngine(withAppId: KeyCenter.appId(), delegate: self)
        
        // 因为是纯音频多人通话的场景，设置为通信模式以获得更好的音质
        agoraKit.setChannelProfile(.communication)
        
        // 通信模式下默认为听筒，demo中将它切为外放
        agoraKit.setDefaultAudioRouteToSpeakerphone(true)
        
        // 启动音量回调，用来在界面上显示房间其他人的说话音量
        agoraKit.enableAudioVolumeIndication(1000, smooth: 3)
        
        // 加入案发现场的群聊频道
        agoraKit.joinChannel(byToken: nil, channelId: KeyCenter.crimeChannelId(), info: nil, uid: 0, joinSuccess: nil)
    }
}

// MARK: UI
private extension CrimeViewController {
    func updateViews() {
        for item in buttons {
            item.imageView?.contentMode = .scaleAspectFit
        }
    }
    
    func removeUser(uid: UInt) {
        for (index, user) in userList.enumerated() {
            if user.uid == uid {
                userList.remove(at: index)
                break
            }
        }
    }
    
    func addUser(uid: UInt) {
        let user = UserInfo.fakeUser(uid: uid)
        userList.append(user)
    }
    
    func updateUser(uid: UInt, isMute: Bool) {
        for (index, user) in userList.enumerated() {
            if user.uid == uid {
                userList[index].isMute = isMute
                break
            }
        }
    }
    
    func getIndexWithUserIsSpeaking(uid: UInt) -> Int? {
        for (index, user) in userList.enumerated() {
            if user.uid == uid {
                return index
            }
        }
        return nil
    }
}

// MARK: AgoraRtcEngineDelegate
extension CrimeViewController: AgoraRtcEngineDelegate {
    func rtcEngine(_ engine: AgoraRtcEngineKit, didJoinChannel channel: String, withUid uid: UInt, elapsed: Int) {
        if agoraStatus.muteAllRemote == true {
            agoraKit.muteAllRemoteAudioStreams(true)
        }
        
        if agoraStatus.muteLocalAudio == true {
            agoraKit.muteLocalAudioStream(true)
        }
        
        // 注意： 1. 由于demo欠缺业务服务器，所以用户列表是根据AgoraRtcEngineDelegate的didJoinedOfUid、didOfflineOfUid回调来管理的
        //       2. 每次加入频道成功后，新建一个用户列表然后通过回调进行统计
        userList = [UserInfo]()
    }
    
    func rtcEngine(_ engine: AgoraRtcEngineKit, didJoinedOfUid uid: UInt, elapsed: Int) {
        // 当有用户加入时，添加到用户列表
        // 注意：由于demo缺少业务服务器，所以当观众加入的时候，观众也会被加入用户列表，并在界面的列表显示成静音状态。 正式实现的话，通过业务服务器可以判断是参与游戏的玩家还是围观观众
        addUser(uid: uid)
    }
    
    func rtcEngine(_ engine: AgoraRtcEngineKit, didOfflineOfUid uid: UInt, reason: AgoraUserOfflineReason) {
        // 当用户离开时，从用户列表中清除
        removeUser(uid: uid)
    }
    
    func rtcEngine(_ engine: AgoraRtcEngineKit, didAudioMuted muted: Bool, byUid uid: UInt) {
        // 当频道里的用户开始或停止发送音频流的时候，会收到这个回调。在界面的用户头像上显示或隐藏静音标记
        updateUser(uid: uid, isMute: muted)
    }
    
    func rtcEngine(_ engine: AgoraRtcEngineKit, reportAudioVolumeIndicationOfSpeakers speakers: [AgoraRtcAudioVolumeInfo], totalVolume: Int) {
        // 收到说话者音量回调，在界面上对应的 cell 显示动效
        for speaker in speakers {
            if let index = getIndexWithUserIsSpeaking(uid: speaker.uid),
                let cell = usersCollectionView.cellForItem(at: IndexPath(item: index, section: 0)) as? UserCell {
                cell.animating = true
            }
        }
    }
}

extension CrimeViewController: UICollectionViewDataSource {
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return userList.count
    }
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "UserCell", for: indexPath) as! UserCell
        let user = userList[indexPath.item]
        cell.nameLabel.text = user.name
        cell.headImgaView.image = user.image
        cell.isMute = user.isMute
        return cell
    }
}
