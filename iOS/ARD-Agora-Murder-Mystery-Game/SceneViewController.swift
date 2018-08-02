//
//  SceneViewController.swift
//  ARD-Agora-Murder-Mystery-Game
//
//  Created by CavanSu on 2018/7/30.
//  Copyright © 2018 Agora. All rights reserved.
//

import UIKit
import AgoraAudioKit

class SceneViewController: UIViewController {
    @IBOutlet weak var voicePlayButton: UIButton!
    @IBOutlet weak var voiceRecordButton: UIButton!
    @IBOutlet weak var bgImageView: UIImageView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet var buttons: [UIButton]!
    
    var scene: Scene!
    var agoraKit: AgoraRtcEngineKit!
    var agoraStatus = AgoraStatus.sharedStatus()
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        updateViews()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        guard let _ = segue.identifier else {
            return
        }
        
        let vc = segue.destination as! PrivateChatViewController
        vc.scene = scene
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
}

// MARK: UI
private extension SceneViewController {
    func updateViews() {
        for item in buttons {
            item.imageView?.contentMode = .scaleAspectFit
        }
        
        voicePlayButton.isSelected = agoraStatus.muteAllRemote
        voiceRecordButton.isSelected = agoraStatus.muteLocalAudio
        
        self.titleLabel.text = scene.title()
        self.bgImageView.image = scene.backgroundImage()
    }
}
