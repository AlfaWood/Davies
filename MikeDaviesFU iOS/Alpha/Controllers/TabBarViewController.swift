//
//  TabBarViewController.swift
//  Alpha
//
//  Created by Razan Nasir on 10/04/18.
//  Copyright © 2018 Stpl. All reserved.
//

import UIKit

class TabBarViewController: UITabBarController {
 
    override func viewDidLoad() {
        super.viewDidLoad()
        let attrsNormal = [
            NSAttributedStringKey.foregroundColor: UIColor.black,
            NSAttributedStringKey.font: UIFont(name:
                "HelveticaNeue-CondensedBold", size: 14)!]
        let attrsSelected = [NSAttributedStringKey.foregroundColor: UIColor.white,
            NSAttributedStringKey.font: UIFont(name: "HelveticaNeue-CondensedBold", size: 14)!]
        UITabBarItem.appearance().setTitleTextAttributes(attrsNormal, for: .normal)
        UITabBarItem.appearance().setTitleTextAttributes(attrsSelected, for: .selected)
        if UIDevice().userInterfaceIdiom == .phone {
            switch UIScreen.main.nativeBounds.height {
            case 2436:
                UITabBar.appearance().selectionIndicatorImage = UIImage().makeImageWithColorAndSize(color: UIColor.init(red: 250/255.0, green: 167/255.0, blue: 127/255.0, alpha: 1.0), size: CGSize(width: tabBar.frame.width/4, height: tabBar.frame.height+30))
            case 2688:
                UITabBar.appearance().selectionIndicatorImage = UIImage().makeImageWithColorAndSize(color: tabTintColor, size: CGSize(width: tabBar.frame.width/4, height: tabBar.frame.height+30))
            case 1792:
                UITabBar.appearance().selectionIndicatorImage = UIImage().makeImageWithColorAndSize(color: tabTintColor, size: CGSize(width: tabBar.frame.width/4, height: tabBar.frame.height+30))
            default:
                UITabBar.appearance().selectionIndicatorImage = UIImage().makeImageWithColorAndSize(color: tabTintColor, size: CGSize(width: tabBar.frame.width/4, height: tabBar.frame.height))
            }
        }
        for item in (self.tabBar.items as [UITabBarItem]?)! {
            if let image = item.image {
                item.image = image.withRenderingMode(.alwaysOriginal)
            }
        }
    }
    
    override var prefersStatusBarHidden: Bool {
        return true
    }
    
    override func tabBar(_ tabBar: UITabBar, didSelect item: UITabBarItem) {
      
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
}
