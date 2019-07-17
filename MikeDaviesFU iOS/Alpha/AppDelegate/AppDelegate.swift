
//  AppDelegate.swift
//  Alpha
//  Created by Razan Nasir on 28/03/18.
//  Copyright © 2018 Stpl. All rights reserved.
//

import UIKit
import Foundation
import Alamofire
import AlamofireObjectMapper
import ObjectMapper
import SVProgressHUD
import UserNotifications
import Firebase
import FirebaseInstanceID
import FirebaseMessaging

@UIApplicationMain

class AppDelegate: UIResponder, UIApplicationDelegate,UNUserNotificationCenterDelegate,MessagingDelegate {
    var window: UIWindow?
    
    let gcmMessageIDKey = "gcm.message_id"
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplicationLaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        UIApplication.shared.isStatusBarHidden = true
        let defaults = UserDefaults.standard
        defaults.set(true, forKey: APP_LAUNCH)
        self.app_Key_Api()
        if UserDefaults.standard.value(forKey: USERLOGGEDIN) != nil &&
            UserDefaults.standard.value(forKey: USERLOGGEDIN) as! Bool == true{
            let feedController = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: TAB_BAR_VIEW_CONTROLLER) as! TabBarViewController
            self.window?.rootViewController = feedController
            self.window?.makeKeyAndVisible()
        }
        FirebaseApp.configure()
        if #available(iOS 10.0, *) {
            // For iOS 10 display notification (sent via APNS)
            UNUserNotificationCenter.current().delegate = self
            
            let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
            UNUserNotificationCenter.current().requestAuthorization(
                options: authOptions,
                completionHandler: {_, _ in })
        } else {
            let settings: UIUserNotificationSettings =
                UIUserNotificationSettings(types: [.alert, .badge, .sound], categories: nil)
            application.registerUserNotificationSettings(settings)
        }
        application.registerForRemoteNotifications()
        // Use Firebase library to configure APIs
        Messaging.messaging().delegate = self
        InstanceID.instanceID().instanceID { (result, error) in
            if let error = error {
                print("Error fetching remote instange ID: \(error)")
            } else if let result = result {
                print("Remote instance ID token: \(result.token)")
            }
        }
        SVProgressHUD.setDefaultMaskType(.black)
        return true
    }
    
    func feedViewOpen(){
        let feedController = UIStoryboard(name: "Main",bundle:nil).instantiateViewController(withIdentifier:TAB_BAR_VIEW_CONTROLLER) as! TabBarViewController
        self.window?.rootViewController = feedController
        self.window?.makeKeyAndVisible()
        
    }
    // The callback to handle data message received via FCM for devices running iOS 10 or above.
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String) {
        print("Firebase registration token: \(fcmToken)")
        let defaults = UserDefaults.standard
        defaults.set(fcmToken, forKey: FCM_TOKEN)
    }
    
    func application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable: Any],
                     fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
        if let messageID = userInfo[gcmMessageIDKey] {
            print("Message ID: \(messageID)")
        }
        if let aps = userInfo["aps"] as? NSDictionary {
            if let alert = aps["alert"] as? NSDictionary {
                if let message = alert["title"] as? NSString {
                    print("Message title: \(message)")
                }
                if let alert = alert["body"] as? NSString {
                    print("Message alert: \(alert)")
                }
            }
        }
        print("user info \(userInfo)")
        print("user info asset \(userInfo["asset"]!)")
        if let dict = convertToDictionary(text: (userInfo["asset"] as? String)!){
            print("User Dict ----->>>> \(String(describing: dict))")
            let defaults = UserDefaults.standard
            defaults.set(true, forKey: COMNIG_FROM_NOTIFICATION)
            defaults.set(dict, forKey: NOTIF_DICT)
            defaults.set(true, forKey: COMNIG_FROM_NOTIFICATION)
            let tabBarController = (self.window!.rootViewController! as! UITabBarController)
            if tabBarController.selectedIndex != 3{
                tabBarController.selectedIndex = 3
            }
        }
        completionHandler(UIBackgroundFetchResult.newData)
    }
    func convertToDictionary(text: String) -> [String: Any]?{
        if let data = text.data(using: .utf8) {
            do {
                return try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any]
            } catch {
                print(error.localizedDescription)
            }
        }
        return nil
    }
    
    func userNotificationCenter(_ center: UNUserNotificationCenter, willPresent notification: UNNotification, withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void){
        let userInfoDict = notification.request.content.userInfo
        completionHandler([.alert, .badge, .sound])
        print("Notification in Foreground : \(userInfoDict)")
        
    }
    
    func applicationWillResignActive(_ application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.
    }
    
    func applicationDidEnterBackground(_ application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    }
    
    func applicationWillEnterForeground(_ application: UIApplication) {
        // Called as part of the transition from the background to the active state; here you can undo many of the changes made on entering the background.
    }
    
    func applicationDidBecomeActive(_ application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    }
    
    func applicationWillTerminate(_ application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    }
    
    //MARK:- GET TOKENIZATION KEY FROM API
    func app_Key_Api(){
        let parameters: Parameters = [APPID:APPIDVALUE]
        Alamofire.request("\(BASE_URL)\(APPKEY_API)", method: .post, parameters: parameters, encoding: JSONEncoding.default, headers: nil).responseObject { (response:DataResponse<User>) in
            if Connectivity.isConnectedToInternet() {
                if response.result.value != nil{
                    let json = response.result.value!
                    switch json.code{
                    case "10"?:
                        let key = json.resultArray?.object(at: 0) as! NSDictionary
                        let keyValue = key[KEY] as! String
                        debugPrint("keyValue: \(keyValue)")
                        let defaults = UserDefaults.standard
                        defaults.set(keyValue, forKey: APP_KEY)
                        break
                    case "0"?:
                        let alertController = UIAlertController(title: json.status, message: json.msg, preferredStyle: .alert)
                        let action1 = UIAlertAction(title: "OK", style: .default) { (action:UIAlertAction) in
                        }
                        alertController.addAction(action1)
                        self.window?.rootViewController?.present(alertController, animated: true, completion: nil)
                    case .none: break
                    case .some(_): break
                    }
                }
                else{
                    let alertController = UIAlertController(title: ALERT, message: SOMESERVERISSUE, preferredStyle: .alert)
                    let action1 = UIAlertAction(title: "OK", style: .default) { (action:UIAlertAction) in
                    }
                    alertController.addAction(action1)
                    self.window?.rootViewController?.present(alertController, animated: true, completion: nil)
                }
            }
            else{
                
                let alertController = UIAlertController(title: ALERT, message: NOINTERNET, preferredStyle: .alert)
                let action1 = UIAlertAction(title: "OK", style: .default) { (action:UIAlertAction) in
                }
                alertController.addAction(action1)
                self.window?.rootViewController?.present(alertController, animated: true, completion: nil)
            }
        }
    }
}





