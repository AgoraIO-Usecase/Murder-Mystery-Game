//
//  UserInfo.swift
//  ARD-Agora-Murder-Mystery-Game
//
//  Created by CavanSu on 2018/7/26.
//  Copyright © 2018 Agora. All rights reserved.
//

import UIKit

struct UserInfo {
    var image: UIImage!
    var name: String!
    var uid: UInt!
    var isMute: Bool!

    static func fakeUser(uid: UInt) -> UserInfo {
        let imageList = [#imageLiteral(resourceName: "head01"), #imageLiteral(resourceName: "head02"), #imageLiteral(resourceName: "head03"), #imageLiteral(resourceName: "head04")]
        let nameList = ["李小姐", "老司机", "张阿姨", "赵铁柱"]
        let rand = Int(arc4random()) % imageList.count
        let image = imageList[rand]
        let name = nameList[rand]
        let user = UserInfo(image: image, name: name, uid: uid, isMute: false)
        return user
    }
}
