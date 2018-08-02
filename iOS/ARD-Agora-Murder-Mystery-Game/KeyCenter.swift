//
//  KeyCenter.swift
//  ARD-Agora-Murder-Mystery-Game
//
//  Created by CavanSu on 2018/7/27.
//  Copyright © 2018 Agora. All rights reserved.
//

import UIKit

class KeyCenter: NSObject {
    static func appId() -> String {
        return <#YOUR APPID#>
    }
    
    static func channelId(scene: Scene) -> String {
        switch scene {
        case .bedroom:
            return "bedroom"
        case .hall:
            return "hall"
        }
    }
    
    static func crimeChannelId() -> String {
        return "crime"
    }
}
