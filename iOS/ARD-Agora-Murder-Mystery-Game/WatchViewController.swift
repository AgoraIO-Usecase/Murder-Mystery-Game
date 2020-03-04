//
//  WatchViewController.swift
//  ARD-Agora-Murder-Mystery-Game
//
//  Created by CavanSu on 2018/7/30.
//  Copyright © 2018 Agora. All rights reserved.
//

import UIKit
import AgoraAudioKit

class WatchViewController: UIViewController {
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
    
    override func viewDidLoad() {
        super.viewDidLoad()
        updateViews()
        loadAgoraKit()
    }
    
    @IBAction func doVoicePlayPressed(_ sender: UIButton) {
        // 开始或者停止播放音频流
        sender.isSelected = !sender.isSelected
        agoraKit.muteAllRemoteAudioStreams(sender.isSelected)
        agoraStatus.muteAllRemote = sender.isSelected
    }
    
    @IBAction func doBackButtonPressed(_ sender: UIButton) {
        // 退出当前剧本，离开群聊频道
        agoraKit.leaveChannel(nil)
        self.navigationController?.popViewController(animated: true)
    }
}

// MARK: AgoraRtcEngineKit
private extension WatchViewController {
    func loadAgoraKit() {
        // 初始化AgoraRtcEngineKit
        agoraKit = AgoraRtcEngineKit.sharedEngine(withAppId: KeyCenter.appId(), delegate: self)
        
        // 因为是纯音频多人通话的场景，设置为通信模式以获得更好的音质
        agoraKit.setChannelProfile(.communication)
        
        // 通信模式下默认为听筒，demo中将它切为外放
        agoraKit.setDefaultAudioRouteToSpeakerphone(true)
        
        // 启动音量回调，用来在界面上显示房间其他人的说话音量
        agoraKit.enableAudioVolumeIndication(1000, smooth: 3, report_vad: false)
        
        // 加入案发现场的群聊频道
        agoraKit.joinChannel(byToken: nil, channelId: KeyCenter.crimeChannelId(), info: nil, uid: 0, joinSuccess: nil)
        
        // 围观模式不发送本地的音频流
        agoraKit.muteLocalAudioStream(true)
    }
}

// MARK: UI
private extension WatchViewController {
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
extension WatchViewController: AgoraRtcEngineDelegate {
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

extension WatchViewController: UICollectionViewDataSource {
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
