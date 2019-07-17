//
//  TableViewCell.swift
//  Alpha
//
//  Created by Monika Tiwari on 10/04/18.
//  Copyright © 2018 Stpl. All rights reserved.
//

import UIKit
import Foundation
import MediaPlayer
import AVFoundation
import AVKit
import youtube_ios_player_helper

class TableViewCell: UITableViewCell {

    @IBOutlet weak var cellDateTimeLbl: UILabel!
    @IBOutlet weak var cellFeedTypeLbl: UILabel!
    @IBOutlet weak var cellDescriptionLbl: UILabel!
    @IBOutlet weak var cellLikeLbl: UILabel!
    @IBOutlet weak var cellCommentsLbl: UILabel!
    @IBOutlet weak var cellImageView: UIImageView!
    
    @IBOutlet var firstCommentLbl: UILabel!
    @IBOutlet var likeImageView: UIImageView!
    @IBOutlet weak var cellImageVideoView: UIImageView!
    @IBOutlet weak var upperLine: UIView!
    @IBOutlet weak var heartBtn: UIButton!
    @IBOutlet weak var commentBtn: UIButton!
    @IBOutlet weak var lowerLine: UIView!
    @IBOutlet weak var readMore: UIButton!
    
    @IBOutlet var readMoreLBL: UILabel!
    @IBOutlet var alphaCommentLbl: UILabel!
    
    @IBOutlet var commentsLbl: UILabel!
    override func awakeFromNib() {
        super.awakeFromNib()
       
        
    }
   

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    
  
   
    
    

}

