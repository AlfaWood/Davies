//
//  Singleton.swift
//  Alpha
//
//  Created by Monika Tiwari on 18/04/18.
//  Copyright © 2018 Stpl. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireObjectMapper
import ObjectMapper
import SVProgressHUD


class Singleton {

    static let sharedInstance = Singleton()
    
    
    var userEmail : String = ""
    var userpassword : String = ""
    
    func refreshToken(arg: Bool, completion: @escaping (Bool,String,String) -> (),failure : @escaping (String , String) -> ()) {
        
        let defaults = UserDefaults.standard
        if let emailID = defaults.string(forKey: EMIALID) {
            userEmail = emailID
        }
        if let password = defaults.string(forKey: USER_PASSWORD) {
            userpassword = password
        }
        print("app key -- \((UserDefaults.standard.string(forKey: APP_KEY))!)")
        
        SVProgressHUD.show()
        let parameters: Parameters = [EMAIL : userEmail, PASSWORD:userpassword,APPID:APPIDVALUE]
        print("parameters \(parameters)")
        Alamofire.request("\(BASE_URL)\(LOGIN_API)", method: .post, parameters: parameters, encoding: JSONEncoding.default, headers: nil).responseObject { (response:DataResponse<User>)  in
            SVProgressHUD.dismiss()
            
            if Connectivity.isConnectedToInternet() {
                if let json = response.result.value
                {
                    if response.result.value != nil
                    {
                        switch json.code{
                        case "10"?:
                            let loginresultDic = json.resultArray?.object(at: 0) as! NSDictionary
                            print("code------\(String(describing: json.code))")
                            print("loginresultDic: \(loginresultDic)")
                            let accessToken = loginresultDic["accessToken"] as! String
                            let defaults = UserDefaults.standard
                            defaults.set(accessToken, forKey: "accessToken")
                            let password_status = loginresultDic[PASSWORD_STATUS] as! String
                            if password_status == COMPLETED {
                                let user = Global(json: loginresultDic)
                                user.saveLoginDetails_IntoUserDefault()
                            }
                            else if password_status == PENDING {
                                
                            }else{

                            }
                            completion(arg,json.code!,json.msg!)
                            SVProgressHUD.dismiss()
                            break
                        case "2"?:
                            SVProgressHUD.dismiss()
                            completion(arg,json.code!,json.msg!)
                            break
                        case "0"?:
                            SVProgressHUD.dismiss()
                            completion(arg,json.code!,json.msg!)
                            break
                        case "3"?:
                            SVProgressHUD.dismiss()
                            completion(arg,json.code!,json.msg!)
                            break
                        case "4"?:
                            SVProgressHUD.dismiss()
                            completion(arg,json.code!,json.msg!)

                            break
                        case .none: break
                        case .some(_): break
                        }
                    }
                    else{
                        failure(ALERT,SOMESERVERISSUE)
                    }
                }
                else{
                        failure(ALERT,SOMESERVERISSUE)
                }
            }
            else{
                        failure(ALERT,NOINTERNET)
            }
            
            SVProgressHUD.dismiss()
        }
        
    }
    
    private  init() {
        
        
        
    }
   
    func requestPOSTURL (success:@escaping (String) -> Void, failure:@escaping (Error) -> Void){
        
        if Connectivity.isConnectedToInternet() {
            let userData = UserDefaults.standard.value(forKey: USERDATA)
            if  userData != nil {
                if(userData as AnyObject).value(forKey: ACCESS_TOKEN) != nil
                {
                    let accessToken = (userData as AnyObject).value(forKey: ACCESS_TOKEN) as! String
                    access_Token = accessToken
                }
                let headers: HTTPHeaders = [AUTHORIZATION: "\(BEARER)\(access_Token!)"]
                Alamofire.request("\(BASE_URL)\(GET_UNREAD_MESSAGE_API)", method: .get, parameters: nil, encoding: JSONEncoding.default, headers:headers ).responseJSON { (response) in
                    if response.result.value != nil
                    {
                        let json = response.result.value! as! NSDictionary
                        switch json[CODE] as! String
                        {
                        case "10":
                            let resultArray = json[RESULT] as! NSArray?
                            print(resultArray!)
                            let keyValue = resultArray?.object(at: 0) as! NSDictionary
                            let unread_Message = keyValue.value(forKey: UNREAD_MESSAGE) as! Int
                            let unread_MessageStr = String(unread_Message)
                            print(unread_MessageStr)
                            success(unread_MessageStr)
                            break
                        case "0": break
                        case "1": break
                        default:
                            break
                        }
                    }
                    else
                    {
                    }
                }
            }
        }
        
    }
    
}
