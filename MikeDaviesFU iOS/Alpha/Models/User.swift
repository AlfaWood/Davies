//
//  User.swift
//  Alpha
//
//  Created by Monika Tiwari on 04/04/18.
//  Copyright © 2018 Stpl. All rights reserved.
//

import UIKit
import Foundation
import ObjectMapper

//class Datas: NSObject,Mappable
//{
//    var data: [Data] = []
//    
//    convenience required init?(map: Map) {
//        self.init()
//    }
//    
//    
//    func mapping(map: Map) {
//        data <- map["data"]
//    }
//
//}



 class User: NSObject,Mappable
{
    
    
    var firstname:String?
    var lastname:String?
    var name:String?
    var email:String?
    var password:String?
    var payment_nonce:String?
    var tokenization_key: String?
    var status: String?
    var msg: String?
    var code: String?
    var url: String?
    var errorString: String?
    //var result:NSDictionary?
    var resultArray:NSArray?
    var resultMutArray:NSMutableArray?
    var totalLikes:Int?
    
    var postComment:Int?
    

    var key:String?
    
    override init() {
        super.init()
    }
    
    convenience required init?(map: Map) {
        self.init()
    }
    
    func mapping(map: Map) {
        
        
        firstname     <- map[FIRSTNAME]
        lastname      <- map[LASTNAME]
        name          <- map[NAME]
        email         <- map[EMAIL]
        password      <- map[PASSWORD]
        payment_nonce <- map[PAYMENT_NONCE]
        tokenization_key         <- map[TOKENIZATION_KEY]
        status         <- map[STATUS]
        msg         <- map[MSG]
        code         <- map[CODE]
        resultArray         <- map[RESULT]
        key         <- map[KEY]
        resultMutArray         <- map[RESULTMUT]
         errorString         <- map[ERRORSTR]
        url         <- map[URL_EULA]
        totalLikes      <- map[TOTAL_LIKES]
        postComment    <- map[COMMENT_COUNT]

        
        
        
    }
    
}

