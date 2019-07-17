//
//  SwitchTabBar.swift
//  Alpha
//
//  Created by Monika Tiwari on 13/04/18.
//  Copyright © 2018 Stpl. All rights reserved.
//

import Foundation
import UIKit

@objc protocol TabBarSwitcher {
    func handleSwipes(sender:UISwipeGestureRecognizer)
}

extension TabBarSwitcher where Self: UIViewController {
    func initSwipe( direction: UISwipeGestureRecognizerDirection){

        let swipe = UISwipeGestureRecognizer(target: self, action: #selector(TabBarSwitcher.handleSwipes(sender:)) )
        swipe.direction = direction
        self.view.addGestureRecognizer(swipe)
    }
    
}

