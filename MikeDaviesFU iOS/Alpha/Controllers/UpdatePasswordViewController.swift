//
//  UpdatePasswordViewController.swift
//  Alpha
//
//  Created by Monika Tiwari on 25/04/18.
//  Copyright © 2018 Stpl. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireObjectMapper
import ObjectMapper
import SVProgressHUD


class UpdatePasswordViewController: UIViewController {
    
    @IBOutlet weak var old_Pwd_Txtfd: UIPaddingExtension!
    @IBOutlet weak var new_Pwd_Txtfd: UIPaddingExtension!
    @IBOutlet weak var re_Entered_Pwd: UIPaddingExtension!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationController?.setNavigationBarHidden(false, animated: false)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        
        /*
         let navView = UIView()
         navView.frame =  CGRect(x:-350, y:-10, width:950, height:65)
         let image = UIImageView()
         image.image = UIImage(named: "DPE-Inline")
         image.frame = CGRect(x:-350, y:-10, width:950, height:65)
         image.contentMode = UIViewContentMode.scaleAspectFit
         navView.sizeToFit()
         // Add both the label and image view to the navView
         navView.addSubview(image)
         
         // Set the navigation bar's navigation item's titleView to the navView
         self.navigationItem.titleView = navView
         
         // Set the navView's frame to fit within the titleView
         navView.sizeToFit()
         */
        
        updateNavigationBarUI()
    }
    
    func updateNavigationBarUI() {
        self.navigationItem.backBarButtonItem = UIBarButtonItem(title: "", style: .plain, target: nil, action: nil)
        self.navigationController?.navigationBar.titleTextAttributes =
            [NSAttributedStringKey.foregroundColor: UIColor.white,
             NSAttributedStringKey.font: UIFont(name: "HelveticaNeue-CondensedBold", size: 30)!]
        
        self.navigationController?.navigationBar.tintColor = navTintColor
        let navBarColor = navigationController!.navigationBar
        
        
        //self.navigationItem.title = "THE WHEN WAY"
        self.navigationItem.title = ""
        
        let imageView = UIImageView(frame: CGRect(x: 0, y: 0, width: 140, height: 26))
        imageView.contentMode = .scaleAspectFit
        let image = UIImage(named: "navLogoCenter.png")
        imageView.image = image
        self.navigationItem.titleView = imageView
        
        self.navigationController?.navigationBar.barTintColor = navTintColor
        self.navigationController?.navigationBar.isTranslucent = false
        navBarColor.barTintColor = navTintColor
        navBarColor.isTranslucent = false
        
        if let image = UIImage(named: "navBarImage.png") {
            let backgroundImage = image.resizableImage(withCapInsets: UIEdgeInsets.zero, resizingMode: .stretch)
            UINavigationBar.appearance().setBackgroundImage(backgroundImage, for: .default)
            self.navigationController?.navigationBar.setBackgroundImage(backgroundImage, for: .default)
            navBarColor.setBackgroundImage(backgroundImage, for: .default)
        }
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    @IBAction func Button_Update_PwdClick(_ sender: UIButton) {
        if old_Pwd_Txtfd.text?.isEmpty == true
        {
            old_Pwd_Txtfd.becomeFirstResponder()
            addAlertView(title: ALERT, message: OLD_PASSWORD_ALERT, buttonTitle: CLICKOK)
        }
        else if old_Pwd_Txtfd.text!.count < 6
        {
            old_Pwd_Txtfd.becomeFirstResponder()
            addAlertView(title: ALERT, message: PASSWORD_LENGTH_ALERT_CHANGE, buttonTitle: CLICKOK)
        }
        else  if new_Pwd_Txtfd.text?.isEmpty == true
        {
            new_Pwd_Txtfd.becomeFirstResponder()
            addAlertView(title: ALERT, message: NEW_PASSWORD_ALERT, buttonTitle: CLICKOK)
        }
        else if new_Pwd_Txtfd.text!.count < 6
        {
            new_Pwd_Txtfd.becomeFirstResponder()
            addAlertView(title: ALERT, message: PASSWORD_LENGTH_ALERT_CHANGE, buttonTitle: CLICKOK)
        }
        else if re_Entered_Pwd.text?.isEmpty == true
        {
            re_Entered_Pwd.becomeFirstResponder()
            addAlertView(title: ALERT, message: CONFIRM_PASSWORD_ALERT, buttonTitle: CLICKOK)
        }
        else if re_Entered_Pwd.text != new_Pwd_Txtfd.text
        {
            re_Entered_Pwd.becomeFirstResponder()
            addAlertView(title: ALERT, message: PASSWORD_CONFIRM_PASSWORD_ALERT, buttonTitle: CLICKOK)
        }
        else
        {
            update_Password_ApiCall()
        }
        
    }
    
    //MARK:- CUSTOME FUNCTIONS
    func update_Password_ApiCall(){
        SVProgressHUD.show()
        let defaults = UserDefaults.standard
        if let accessToken = defaults.string(forKey: ACCESS_TOKEN) {
            access_Token = accessToken
        }
        let headers: HTTPHeaders = ["Authorization": "\("Bearer ")\(access_Token!)"]
        let parameters: Parameters = [OLD_PASSWORD :old_Pwd_Txtfd.text! , NEW_PASSWORD: new_Pwd_Txtfd.text!,RE_ENTERED_PASSWORD :re_Entered_Pwd.text!]
        Alamofire.request("\(BASE_URL)\(UPDATE_PASSWORD_API)", method: .post, parameters: parameters, encoding: JSONEncoding.default, headers: headers).responseObject { (response:DataResponse<User>)  in
            SVProgressHUD.dismiss()
            if Connectivity.isConnectedToInternet() {
                if response.result.value != nil
                {
                    let json = response.result.value!
                    switch json.code{
                    case "10"?:
                        let alert = UIAlertController(title: json.status!, message: json.msg!, preferredStyle: UIAlertControllerStyle.alert)
                        
                        alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: { (action: UIAlertAction!) in
                            UserDefaults.standard.set(false, forKey: USERLOGGEDIN)
                            let vc=self.storyboard?.instantiateViewController(withIdentifier: LOGIN_VIEW_CONTROLLER) as! LoginViewController
                            self.navigationController?.pushViewController(vc, animated: true)
                            
                        }))
                        self.present(alert, animated: true, completion: nil)
                        break
                    case "0"?: break
                    case "401"?:
                        Singleton.sharedInstance.refreshToken(arg: true, completion: { (success,jsonCode,jsonMsg)-> Void in
                            print("Second line of code executed")
                            if success { // this will be equal to whatever value is set in this method call
                                self.update_Password_ApiCall()
                            } else {
                            }
                        }, failure: {(alert,message) -> Void in
                            self.addAlertView(title: alert, message: message, buttonTitle: CLICKOK)
                        }
                        )
                        break
                    case .none: break
                    case .some(_):
                        self.addAlertView(title: ALERT, message:json.msg!, buttonTitle: CLICKOK)
                        break
                    }
                }
                else{
                    self.addAlertView(title: ALERT, message: SOMESERVERISSUE, buttonTitle: CLICKOK)
                }
            }
            else{
                self.addAlertView(title: ALERT, message: NOINTERNET, buttonTitle: CLICKOK)
            }
        }
    }
    
}
