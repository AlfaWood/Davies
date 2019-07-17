//
//  Global.swift
//  Alpha
//
//  Created by Monika Tiwari on 25/04/18.
//  Copyright © 2018 Stpl. All rights reserved.
//

import UIKit

class Global: NSObject {

    var firstname:String?
    var lastname:String?
    var name:String?
    var email:String?
    var password:String?
    var passwordStatus:String?
    var token:String?
    var accessToken:String?
    var payment_nonce:String?
    var tokenization_key: String?
    var status: String?
    var msg: String?
    var code: String?
    //var result:NSDictionary?
    var resultArray:NSArray?
    var resultMutArray:NSMutableArray?
    
    init(json: NSDictionary) {
        
        if let name = json[NAME] as? String {
            self.name = name
        }
        if let email = json[EMAIL] as? String {
            self.email = email
        }
        if let passwordStatus = json[PASSWORD_STATUS] as? String {
            self.passwordStatus = passwordStatus
        }
        if let passwordStatus = json[PASSWORD_STATUS] as? String {
            self.passwordStatus = passwordStatus
        }
        if let token = json[TOKEN] as? String {
            self.token = token
        }
        if let accessToken = json[ACCESS_TOKEN] as? String {
            self.accessToken = accessToken
        }
    }
    
    func saveLoginDetails_IntoUserDefault() {
        //
        let userData = NSDictionary(objects: [self.name!,self.email!,self.passwordStatus!,self.token!,self.accessToken!], forKeys: [NAME as NSCopying, EMAIL as NSCopying, PASSWORD_STATUS as NSCopying,TOKEN as NSCopying,ACCESS_TOKEN as NSCopying])
        UserDefaults.standard.setValue(userData, forKey: USERDATA)
        UserDefaults.standard.set(true, forKey: USERLOGGEDIN)
        UserDefaults.standard.synchronize()
        
    }
    
    
    
}
