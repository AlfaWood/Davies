///// Copyright (c) 2017 Razeware LLC
/////
///// Permission is hereby granted, free of charge, to any person obtaining a copy
///// of this software and associated documentation files (the "Software"), to deal
///// in the Software without restriction, including without limitation the rights
///// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
///// copies of the Software, and to permit persons to whom the Software is
///// furnished to do so, subject to the following conditions:
/////
///// The above copyright notice and this permission notice shall be included in
///// all copies or substantial portions of the Software.
/////
///// Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
///// distribute, sublicense, create a derivative work, and/or sell copies of the
///// Software in any work that is designed, intended, or marketed for pedagogical or
///// instructional purposes related to programming, coding, application development,
///// or information technology.  Permission for such use, copying, modification,
///// merger, publication, distribution, sublicensing, creation of derivative works,
///// or sale is expressly withheld.
/////
///// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
///// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
///// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
///// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
///// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
///// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
///// THE SOFTWARE.
//
//import Foundation
//import Alamofire
//
//enum Result {
//  case success (String)
//  case failure(String)
//}
//
//final class StripeClient {
//
//  static let shared = StripeClient()
//
//  private init() {
//    // private
//  }
//
//  private lazy var baseURL: URL = {
//    guard let url = URL(string: Constants.baseURLString) else {
//      fatalError("Invalid URL")
//    }
//    return url
//  }()
//
//    func completeCharge(with token: STPToken, amount: String, registrationDict : NSMutableDictionary, planID : String , completion: @escaping (Result) -> Void) {
//        // 1
//        // 2
//        let str = UserDefaults.standard.value(forKey: FCM_TOKEN)
//
//        print("TOKEN : \(str!)")
//
//        let params: [String: Any] = [
//            "token": token.tokenId,
//            "amount": amount,
//            "first_name": registrationDict[FIRSTNAME]!,
//            "last_name": registrationDict[LASTNAME]!,
//            "name": registrationDict[NAME]!,
//            "email": registrationDict[EMAIL]!,
//            "password": registrationDict[PASSWORD]!,
//            "plan_id": planID,
//            "app_id": registrationDict[APPID]!,
//            "device_token": str!,
//            "device_type": "ios"
//
//        ]
//
//        print("parameters -- \(params)")
//        Alamofire.request("\(BASE_URL)\(REGISTRATION_API)", method: .post, parameters: params, encoding: JSONEncoding.default, headers: nil).responseObject { (response:DataResponse<User>)  in
//            if Connectivity.isConnectedToInternet() {
//                if response.result.value != nil
//                {
//                    let json = response.result.value!
//                    print("JSON : \(json)")
//                    switch json.code{
//                    case "10"?:
//
//                        let resultDic = json.resultArray?.object(at: 0) as! NSDictionary
//                        debugPrint("resultDic: \(resultDic)")
//                         completion(Result.success(json.msg!))
//                        let accessToken = resultDic["accessToken"] as! String
//                        let defaults = UserDefaults.standard
//                        defaults.set(accessToken, forKey: "accessToken")
//                        let custID = resultDic["customer_id"] as! String
//                        defaults.set(custID, forKey:CUSTOMER_ID)
//                        let profilePic = resultDic["profile_pic"] as! String
//                        defaults.set(profilePic, forKey:PROFILE_PIC)
//                        let SubscriptionID = resultDic["subscription_id"] as! String
//                        defaults.set(SubscriptionID, forKey:SUBSCRIPTION_ID)
//                        let userName =  String(format:"%@ %@",resultDic["first_name"] as! String,resultDic["last_name"] as! CVarArg)
//                        defaults.set(userName, forKey:UERS_NAME_COMMENT)
//                        defaults.set(true, forKey: APP_LAUNCH)
//                        defaults.set("1", forKey:IS_FOLLOWER)
//                        break
//                    case "0"?:
//                        completion(Result.failure((json.msg)!))
//                        break
//                    case "1"?:
//                        completion(Result.failure((json.msg)!))
//                        break
//                    case "500"?:
//                         completion(Result.failure((json.msg)!))
//                        break
//                    case "17"?:
//                        completion(Result.failure((json.msg)!))
//                        break
//                    case .none:
//                        completion(Result.failure((json.msg)!))
//                        break
//                    case .some(_):
//                         completion(Result.failure((json.msg)!))
//                        break
//                    }
//                }
//                else
//                {
//                }
//            }
//            else{
//            }
//        }
//
//    }
//
//
//}
