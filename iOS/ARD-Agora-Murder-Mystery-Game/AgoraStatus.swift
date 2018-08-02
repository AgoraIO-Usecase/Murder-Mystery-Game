//
//  AgoraStatus.swift
//  ARD-Agora-Murder-Mystery-Game
//
//  Created by CavanSu on 2018/7/30.
//  Copyright © 2018 Agora. All rights reserved.
//

import UIKit
import AgoraAudioKit

// MARK: AograStatus stores AgoraRtcEngineKit some statuses
class AgoraStatus: NSObject {
    static let agoraStatus = AgoraStatus()
    var muteLocalAudio: Bool = false
    var muteAllRemote: Bool = false
    
    static func sharedStatus() -> AgoraStatus {
        return agoraStatus
    }
}
