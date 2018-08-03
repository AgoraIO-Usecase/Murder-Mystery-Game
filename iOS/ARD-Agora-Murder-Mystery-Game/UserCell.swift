//
//  UserCell.swift
//  ARD-Agora-Murder-Mystery-Game
//
//  Created by CavanSu on 2018/7/27.
//  Copyright © 2018 Agora. All rights reserved.
//

import UIKit

class UserCell: UICollectionViewCell {
    @IBOutlet weak var headImgaView: UIImageView!
    @IBOutlet weak var nameLabel: UILabel!
    var volumeLayer = CALayer.init()
    lazy var keyAnimation: CAKeyframeAnimation = {() -> CAKeyframeAnimation in
        let animate = CAKeyframeAnimation.init()
        animate.keyPath = "opacity"
        animate.values = [0, 0.2, 0.5, 0.2, 0]
        animate.duration = 2
        animate.repeatCount = 1
        return animate
    }()
    
    lazy var muteImageView: UIImageView = {() -> UIImageView in
        let imageView = UIImageView()
        imageView.image = #imageLiteral(resourceName: "mrcophone-s")
        self.addSubview(imageView)
        return imageView
    }()
    
    var isMute: Bool! {
        didSet {
            muteImageView.isHidden = !isMute
        }
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        self.backgroundColor = UIColor.clear
        headImgaView.layer.insertSublayer(volumeLayer, at: 0)
        volumeLayer.borderWidth = 3
        volumeLayer.borderColor = UIColor.init(red: 210 / 255, green: 153 / 255, blue: 95 / 255, alpha: 1).cgColor
        volumeLayer.opacity = 0
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        
        let r = self.bounds.width * 0.5
        let d = sqrt(r * r * 0.5)
        let imageWH: CGFloat = 15.0
        let imageXY = d + r - (imageWH * 0.5)
        muteImageView.frame = CGRect(x: imageXY, y: imageXY, width: imageWH, height: imageWH)
        
        volumeLayer.frame = headImgaView.bounds
        volumeLayer.cornerRadius = headImgaView.bounds.height * 0.5
    }
    
    var animating: Bool! {
        didSet {
            if animating == true, oldValue == false || oldValue == nil {
                keyAnimation.delegate = self
                volumeLayer.add(keyAnimation, forKey: nil)
            }
        }
    }
}

extension UserCell: CAAnimationDelegate {
    func animationDidStop(_ anim: CAAnimation, finished flag: Bool) {
        if flag == true {
            animating = false
            keyAnimation.delegate = nil
            volumeLayer.removeAllAnimations()
        }
    }
}

