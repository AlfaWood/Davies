//
//  RegistrationViewController.swift
//  Alpha
//
//  Created by Monika Tiwari on 30/03/18.
//  Copyright © 2018 Stpl. All rights reserved.
//

import UIKit
import SVProgressHUD
import Alamofire
import AlamofireObjectMapper
import ObjectMapper

class RegistrationViewControllerOne: UIViewController {
    
    //OUTLETS
    @IBOutlet weak var navHeaderLbl: UILabel!
    @IBOutlet weak var emailTextFd: UIPaddingExtension!
    @IBOutlet weak var passwordTextfd: UIPaddingExtension!
    @IBOutlet weak var reenterPwdTextfd: UIPaddingExtension!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.hideKeyboardWhenTappedAround()
    }
    
    override func viewWillAppear(_ animated: Bool) {
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
        
        let retrieveDict = UserDefaults.standard.dictionary(forKey:"userDictFirst")
        if retrieveDict != nil
        {
            emailTextFd.text = retrieveDict?["email"] as? String
            passwordTextfd.text = retrieveDict?["password"] as? String
            reenterPwdTextfd.text = retrieveDict?["recentpass"] as? String
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
    
    // MARK: - Detail Varification
    
    /**
     * Method name: isValidEmail
     * Description: This method is used for email validation.
     * Parameters : testStr:String
     * Return     : Bool
     */
    
    func isValidEmail(testStr:String) -> Bool {
        print("validate emilId: \(testStr)")
        let emailRegEx = EMAIL_VARIFICATIONT_TEXT
        let emailTest = NSPredicate(format:"SELF MATCHES %@", emailRegEx)
        let result = emailTest.evaluate(with: testStr)
        return result
    }
    
    //MARK:- BUTTONS ACTION
    // PROCEED BUTTON
    @IBAction func proceedButtonAction(_ sender: UIButton) {
        if emailTextFd.text?.isEmpty == true
        {
            emailTextFd.becomeFirstResponder()
            addAlertView(title: ALERT, message: EMAIL_ALERT, buttonTitle: CLICKOK)
        }
        else if isValidEmail(testStr: emailTextFd.text!) == false
        {
            emailTextFd.becomeFirstResponder()
            addAlertView(title: ALERT, message: VALID_EMAIL_ALERT, buttonTitle: CLICKOK)
        }
        else if passwordTextfd.text?.isEmpty == true
        {
            passwordTextfd.becomeFirstResponder()
            addAlertView(title: ALERT, message: PASSWORD_ALERT, buttonTitle: CLICKOK)
        }
        else if passwordTextfd.text!.count < 6 {
            passwordTextfd.becomeFirstResponder()
            addAlertView(title: ALERT, message: PASSWORD_LENGTH_ALERT, buttonTitle: CLICKOK)
        }
        else if passwordTextfd.text!.count > 20 {
            passwordTextfd.becomeFirstResponder()
            addAlertView(title: ALERT, message: PASSWORD_LENGTH_ALERT_LONG, buttonTitle: CLICKOK)
        }
        else if reenterPwdTextfd.text?.isEmpty == true
        {
            reenterPwdTextfd.becomeFirstResponder()
            addAlertView(title: ALERT, message: CONFIRM_PASSWORD_ALERT, buttonTitle: CLICKOK)
        }
        else if reenterPwdTextfd.text != passwordTextfd.text
        {
            reenterPwdTextfd.becomeFirstResponder()
            addAlertView(title: ALERT, message: PASSWORD_CONFIRM_PASSWORD_ALERT, buttonTitle: CLICKOK)
        }
        else
        {
            RegistrationApiCallEmail(email:emailTextFd.text!)
        }
        
        
    }
    
    // BACK BUTTON
    @IBAction func backButtonAction(_ sender: UIButton) {
        self.navigationController?.popViewController(animated: true)
    }
    
    // REGISTRATION API FOR EMAIL
    
    func RegistrationApiCallEmail(email:String){
        SVProgressHUD.show()
        let parameters: Parameters = [EMAIL:email,APPID:APPIDVALUE]
        Alamofire.request("\(BASE_URL)\(CHECK_EMAIL_API)", method: .post, parameters: parameters, encoding: JSONEncoding.default, headers: nil).responseObject { (response:DataResponse<User>)  in
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
                        self.navigateToRegistration_Two_Screen()
                    case "1"?:
                        self.navigateToRegistration_Two_Screen()
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
    
    func navigateToRegistration_Two_Screen()  {
        let vc=self.storyboard?.instantiateViewController(withIdentifier: REGISTRATION_VIEW_CONTROLLER_TWO) as! RegistrationViewControllerTwo
        vc.emailStr = emailTextFd.text
        vc.passwordStr = passwordTextfd.text
        vc.reEnterPwdStr = reenterPwdTextfd.text
        var userDictforFirtSS = [String : String]()
        userDictforFirtSS = ["email":emailTextFd.text,"password":passwordTextfd.text,"recentpass":reenterPwdTextfd.text] as! [String : String]
        UserDefaults.standard.set(userDictforFirtSS, forKey: "userDictFirst")
        self.navigationController?.pushViewController(vc, animated: true)
    }
}
