//
//  RegistrationViewControllerTwo.swift
//  Alpha
//
//  Created by Monika Tiwari on 06/04/18.
//  Copyright © 2018 Stpl. All rights reserved.
//

import UIKit
import SVProgressHUD
import Alamofire
import AlamofireObjectMapper
import ObjectMapper

class RegistrationViewControllerTwo: UIViewController, UITextFieldDelegate {
    
    //OUTLETS
    @IBOutlet weak var navHeaderLbl: UILabel!
    @IBOutlet weak var firstnameTextfd: UITextField!
    @IBOutlet weak var lastNameTextfd: UITextField!
    @IBOutlet weak var userNameTextfd: UITextField!
    
    // VARIABLES
    var tokenization_key:String?
    var payment_nonce:String?
    var emailStr: String?
    var passwordStr: String?
    var reEnterPwdStr:String?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        let defaults = UserDefaults.standard
        
        if let tokenizationKey = defaults.string(forKey: TOKENIZATION_KEY) {
            tokenization_key = tokenizationKey
        }
        userNameTextfd.delegate = self
        self.hideKeyboardWhenTappedAround()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        UserDefaults.standard.set("https://iamanalpha1.com/images/default.png", forKey:PROFILE_PIC)
        
        /*
         let navView = UIView()
         // Create the label
         let label = UILabel()
         label.text = APP_NAME
         label.frame = CGRect(x:0, y: 0, width: 180, height:50)
         label.center = navView.center
         label.textAlignment = NSTextAlignment.right
         label.textColor = UIColor.white
         label.font = UIFont(name: "HelveticaNeue-CondensedBold", size: 24)
         // Create the image view
         let image = UIImageView()
         image.image = UIImage(named: "icon_palumbo")
         // To maintain the image's aspect ratio:
         let imageAspect = image.image!.size.width/image.image!.size.height
         // Setting the image frame so that it's immediately before the text:
         image.frame = CGRect(x: label.frame.origin.x-label.frame.size.height*imageAspect+50, y: label.frame.origin.y, width: label.frame.size.height*imageAspect-20, height: label.frame.size.height)
         image.contentMode = UIViewContentMode.scaleAspectFit
         
         // Add both the label and image view to the navView
         navView.addSubview(label)
         navView.addSubview(image)
         
         // Set the navigation bar's navigation item's titleView to the navView
         self.navigationItem.titleView = navView
         
         // Set the navView's frame to fit within the titleView
         navView.sizeToFit()
         */
        
        updateNavigationBarUI()
        
        navigationController?.navigationBar.isHidden = true
        
        let retrieveDict = UserDefaults.standard.dictionary(forKey:"userDictSecond")
        if retrieveDict != nil
        {
            firstnameTextfd.text = retrieveDict?["firstname"] as? String
            lastNameTextfd.text = retrieveDict?["lastname"] as? String
            userNameTextfd.text = retrieveDict?["username"] as? String
        }
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
        
        navHeaderLbl.font = UIFont(name: "HelveticaNeue-CondensedBold", size: 30)!
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool
    {
        if(textField == firstnameTextfd)
        {
            lastNameTextfd.becomeFirstResponder()
        }
        else if(textField == lastNameTextfd)
        {
            userNameTextfd.becomeFirstResponder()
        }
        else
        {
            textField.returnKeyType = UIReturnKeyType.done
            textField.resignFirstResponder()
        }
        return false
    }
    
    //MARK:- BUTTONS ACTION
    // PROCEED BUTTON
    @IBAction func proceedButtonAction(_ sender: UIButton) {
        if firstnameTextfd.text?.isEmpty == true
        {
            firstnameTextfd.becomeFirstResponder()
            addAlertView(title: ALERT, message: FIRSTNAME_ALERT, buttonTitle: CLICKOK)
        }
        else if(firstnameTextfd.text?.trimmingCharacters(in: .whitespaces).isEmpty)!
        {
            addAlertView(title: ALERT, message: MESSAGE_BLANK_FIRST, buttonTitle: CLICKOK)
        }
        else if lastNameTextfd.text?.isEmpty == true
        {
            lastNameTextfd.becomeFirstResponder()
            addAlertView(title: ALERT, message: LASTNAME_ALERT, buttonTitle: CLICKOK)
        }
        else if(lastNameTextfd.text?.trimmingCharacters(in: .whitespaces).isEmpty)!
        {
            addAlertView(title: ALERT, message: MESSAGE_BLANK_LAST, buttonTitle: CLICKOK)
        }
        else if userNameTextfd.text?.isEmpty == true
        {
            userNameTextfd.becomeFirstResponder()
            addAlertView(title: ALERT, message: USER_NAME_ALERT, buttonTitle: CLICKOK)
        }
        else if(userNameTextfd.text?.trimmingCharacters(in: .whitespaces).isEmpty)!
        {
            addAlertView(title: ALERT, message: MESSAGE_BLANK_USER, buttonTitle: CLICKOK)
        }
        else  if (userNameTextfd.text?.rangeOfCharacter(from: CHARACTERSET.inverted) != nil) {
            userNameTextfd.becomeFirstResponder()
            addAlertView(title: ALERT, message: VALID_USER_ALERT, buttonTitle: CLICKOK)
        }
        else if userNameTextfd.text!.count < 6
        {
            userNameTextfd.becomeFirstResponder()
            addAlertView(title: ALERT, message: VALID_USER_ALERT, buttonTitle: CLICKOK)
        }
        else
        {
            
            RegistrationApiCallEmail(userName:userNameTextfd.text!)
        }
        
    }
    
    
    func RegistrationApiCallEmail(userName:String){
        SVProgressHUD.show()
        let parameters: Parameters = [USER_NAME:userName,APPID:APPIDVALUE,DEVICE_TYPE:"ios"]
        Alamofire.request("\(BASE_URL)\(CHECK_USER_NAME)", method: .post, parameters: parameters, encoding: JSONEncoding.default, headers: nil).responseObject { (response:DataResponse<User>)  in
            SVProgressHUD.dismiss()
            if Connectivity.isConnectedToInternet() {
                if response.result.value != nil
                {
                    let json = response.result.value!
                    switch json.code{
                    case "10"?:
                        self.addAlertView(title:ALERT, message:json.msg!, buttonTitle: CLICKOK)
                        break
                    case "0"?:
                        print("URL IS \(String(describing: json.url))")
                        UserDefaults.standard.set(json.url, forKey: URL_TERMS)
                        self.navigateToSubscriptionPlan()
                    case "1"?:
                        self.navigateToSubscriptionPlan()
                    case .none: break
                    case .some(_): break
                    }
                }
                else
                {
                    self.addAlertView(title: ALERT, message: SOMESERVERISSUE, buttonTitle: CLICKOK)
                }
            }
            else{
                self.addAlertView(title: ALERT, message: SOMESERVERISSUE, buttonTitle: CLICKOK)
            }
        }
    }
    
    
    @IBAction func backButtonAction(_ sender: UIButton) {
        self.navigationController?.popViewController(animated: true)
    }
    
    // REGISTRATION API
    func RegistrationApiCall(payment_nonce:String){
        SVProgressHUD.show()
        let parameters: Parameters = [FIRSTNAME :firstnameTextfd.text! , LASTNAME: lastNameTextfd.text!, NAME : userNameTextfd.text!, EMAIL:emailStr!,PASSWORD:passwordStr! ,PAYMENT_NONCE:payment_nonce, APPID:APPIDVALUE]
        
        Alamofire.request("\(BASE_URL)\(REGISTRATION_API)", method: .post, parameters: parameters, encoding: JSONEncoding.default, headers: nil).responseObject { (response:DataResponse<User>)  in
            SVProgressHUD.dismiss()
            if Connectivity.isConnectedToInternet() {
                if response.result.value != nil
                {
                    let json = response.result.value!
                    switch json.code{
                    case "10"?:
                        let resultDic = json.resultArray?.object(at: 0) as! NSDictionary
                        debugPrint("resultDic: \(resultDic)")
                        
                        let accessToken = resultDic["accessToken"] as! String
                        let defaults = UserDefaults.standard
                        defaults.set(accessToken, forKey: "accessToken")
                        self.navigateToFeedScreen()
                        break
                    case "0"?:
                        self.addAlertView(title: json.status!, message: json.msg!, buttonTitle: CLICKOK)
                    case "1"?:
                        self.addAlertView(title: json.status!, message: json.msg!, buttonTitle: CLICKOK)
                    case .none: break
                    case .some(_): break
                    }
                }
                else
                {
                    self.addAlertView(title: ALERT, message: SOMESERVERISSUE, buttonTitle: CLICKOK)
                }
            }
            else{
                self.addAlertView(title: ALERT, message: SOMESERVERISSUE, buttonTitle: CLICKOK)
            }
        }
    }
    
    func navigateToFeedScreen()  {
        let appDelegate = UIApplication.shared.delegate as! AppDelegate
        appDelegate.feedViewOpen()
        
    }
    
    func navigateToSubscriptionPlan()  {
        
        var userDictforFirtSS = [String : String]()
        userDictforFirtSS = ["firstname":firstnameTextfd.text,"lastname":lastNameTextfd.text,"username":userNameTextfd.text] as! [String : String]
        UserDefaults.standard.set(userDictforFirtSS, forKey: "userDictSecond")
        
        let parameters: NSMutableDictionary = [FIRSTNAME :firstnameTextfd.text! , LASTNAME: lastNameTextfd.text!, NAME : userNameTextfd.text!, EMAIL:emailStr!,PASSWORD:passwordStr!, APPID:APPIDVALUE]
        let vc = self.storyboard?.instantiateViewController(withIdentifier: TERMS_PLANVC) as! TermsVC
        vc.userparams = parameters
        show(vc, sender: self)
        
        
    }
}

