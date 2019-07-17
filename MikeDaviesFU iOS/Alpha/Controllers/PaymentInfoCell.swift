//
//  PaymentInfoCell.swift
//  StripeDemo
//
//  Created by Akash Verma on 6/8/18.
//  Copyright © 2018 Chandra Mouli Shukla. All rights reserved.
//

import UIKit

class PaymentInfoCell: UITableViewCell {
    
 
    @IBOutlet weak var mainViewCell: UIView!
    
    
     @IBOutlet weak var monthView: UIView!
    @IBOutlet weak var btnSubscribe: UIButton!

    override func awakeFromNib() {
        super.awakeFromNib()
        
        btnSubscribe.layer.masksToBounds = true;

    }
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        
    }

}
