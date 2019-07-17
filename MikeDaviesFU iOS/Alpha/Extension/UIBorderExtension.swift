//
//  UIBorderExtension.swift
//  Alpha
//
//  Created by Monika Tiwari on 29/03/18.
//  Copyright © 2018 Stpl. All rights reserved.
//

import Foundation
import UIKit


// MARK: - CUSTOM UIVIEW BORDER
extension UIView {
    
    
    
    @IBInspectable var cornerRadius: CGFloat {
        get {
            return layer.cornerRadius
        }
        set {
            layer.cornerRadius = newValue
            layer.masksToBounds = newValue > 0
        }
    }
    
    @IBInspectable var borderWidth: CGFloat {
        get {
            return layer.borderWidth
        }
        set {
            layer.borderWidth = newValue
        }
    }
    
    @IBInspectable var borderColor: UIColor? {
        get {
            return UIColor(cgColor: layer.borderColor!)
        }
        set {
            layer.borderColor = newValue?.cgColor
        }
    }
    
    
    
}

