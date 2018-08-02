//
//  MainViewController.swift
//  ARD-Agora-Murder-Mystery-Game
//
//  Created by CavanSu on 2018/7/26.
//  Copyright © 2018 Agora. All rights reserved.
//

import UIKit

class MainViewController: UIViewController {
    @IBOutlet var buttons: [UIButton]!
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        updateViews()
    }
    
    func updateViews() {
        for item in buttons {
            item.imageView?.contentMode = .scaleAspectFit
        }
    }
}

