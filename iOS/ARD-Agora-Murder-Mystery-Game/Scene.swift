//
//  Scene.swift
//  ARD-Agora-Murder-Mystery-Game
//
//  Created by CavanSu on 2018/7/30.
//  Copyright © 2018 Agora. All rights reserved.
//

import UIKit

enum Scene {
    case bedroom, hall
}

extension Scene {
    func title() -> String? {
        switch self {
        case .bedroom:
            return "卧室"
        case .hall:
            return "走廊"
        }
    }
    
    func backgroundImage() -> UIImage? {
        switch self {
        case .bedroom:
            return #imageLiteral(resourceName: "bg04")
        case .hall:
            return #imageLiteral(resourceName: "bg02")
        }
    }
    
    func privateChannelId() -> String? {
        switch self {
        case .bedroom:
            return "bedroom"
        case .hall:
            return "hall"
        }
    }
}
