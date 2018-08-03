//
//  PrivateChatViewController.swift
//  ARD-Agora-Murder-Mystery-Game
//
//  Created by CavanSu on 2018/7/30.
//  Copyright © 2018 Agora. All rights reserved.
//

import UIKit
import AgoraAudioKit

class PrivateChatViewController: UIViewController {
    @IBOutlet weak var voicePlayButton: UIButton!
    @IBOutlet weak var voiceRecordButton: UIButton!
    @IBOutlet weak var bgImageView: UIImageView!
    @IBOutlet weak var usersCollectionView: UICollectionView!
    
    var userList = [UserInfo]() {
        didSet {
            usersCollectionView?.reloadData()
        }
    }
    
    var scene: Scene!
    var agoraKit: AgoraRtcEngineKit!
    var agoraStatus = AgoraStatus.sharedStatus()
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        loadAgoraKit()
        updateViews()
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
        // 退出私聊界面，离开私聊频道
        agoraKit.leaveChannel(nil)
    }
}

// MARK: AgoraRtcEngineKit
private extension PrivateChatViewController {
    func loadAgoraKit() {
        // 初始化AgoraRtcEngineKit
        agoraKit = AgoraRtcEngineKit.sharedEngine(withAppId: KeyCenter.appId(), delegate: self)
        agoraKit.leaveChannel(nil)
        agoraKit.setChannelProfile(.communication)
        
        // 通信模式下默认为听筒，demo中将它切为外放
        agoraKit.setDefaultAudioRouteToSpeakerphone(true)
        
        // 加入私聊频道
        agoraKit.joinChannel(byToken: nil, channelId: KeyCenter.channelId(scene: scene), info: nil, uid: 0, joinSuccess: nil)
    }
}

// MARK: UI
private extension PrivateChatViewController {
    func updateViews() {
        voicePlayButton.isSelected = agoraStatus.muteAllRemote
        voiceRecordButton.isSelected = agoraStatus.muteLocalAudio
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
extension PrivateChatViewController: AgoraRtcEngineDelegate {
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

extension PrivateChatViewController: UICollectionViewDataSource {
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
