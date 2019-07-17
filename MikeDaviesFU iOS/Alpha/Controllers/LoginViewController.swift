//
//  ViewController.swift
//  Created by Razan Nasir on 28/03/18.
//  Copyright © 2018 Stpl. All rights reserved.
//

import UIKit
import Alamofire
import AlamofireObjectMapper
import ObjectMapper
import SVProgressHUD

class LoginViewController: UIViewController, UITextFieldDelegate {
    //OUTLETS
    @IBOutlet weak var labelAlert: UILabel!
    @IBOutlet weak var userNameTxtfd: UITextField!
    @IBOutlet weak var passwordTxtfd: UITextField!
    @IBOutlet weak var buttonSignup: UIButton!
    @IBOutlet weak var loginTopUIView: UIView!
    @IBOutlet weak var loginBottomView: UIView!
    @IBOutlet weak var forgotPswdLabel: UILabel!
    @IBOutlet weak var scrollView: UIScrollView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        labelAlert.isHidden = true
        self.hideKeyboardWhenTappedAround()
        let forgot_lbl_tap_gesture = UITapGestureRecognizer(target: self, action: #selector(LoginViewController.handleForgotPasswordTap(sender:)) )
        forgotPswdLabel.isUserInteractionEnabled = true
        forgotPswdLabel.addGestureRecognizer(forgot_lbl_tap_gesture)
        NotificationCenter.default.addObserver(self, selector: #selector(LoginViewController.keyboardWillHide), name: NSNotification.Name.UIKeyboardWillHide, object: nil)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        updateNavigationBarUI()
        
        self.buttonSignup.isUserInteractionEnabled = false
        navigationController?.navigationBar.isHidden = true
        
        self.tabBarController?.tabBar.isHidden = true
        self.navigationController?.setNavigationBarHidden(true, animated: false)
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
    
    @objc func keyboardWillHide(notification: NSNotification) {
        if let keyboardSize = (notification.userInfo?[UIKeyboardFrameBeginUserInfoKey] as? NSValue)?.cgRectValue {
            if self.view.frame.origin.y != 0{
                self.view.frame.origin.y += keyboardSize.height
            }
            self.scrollView.setContentOffset(CGPoint(x: 0, y: 0), animated: false)
            }
    }
    
    func isValidEmail(testStr:String) -> Bool {
        let emailRegEx = EMAIL_VARIFICATIONT_TEXT
        let emailTest = NSPredicate(format:"SELF MATCHES %@", emailRegEx)
        let result = emailTest.evaluate(with: testStr)
        return result
    }
    
    //MARK:- FORGOTPASS HANDLER
    @objc func handleForgotPasswordTap(sender:UITapGestureRecognizer){
        if userNameTxtfd.text?.isEmpty == true{
            userNameTxtfd.becomeFirstResponder()
            addAlertView(title: ALERT, message: EMAIL_ALERT, buttonTitle: CLICKOK)
        }
        else if isValidEmail(testStr: userNameTxtfd.text!) == false{
            userNameTxtfd.becomeFirstResponder()
            addAlertView(title: ALERT, message: VALID_EMAIL_ALERT, buttonTitle: CLICKOK)
        }
        else{
            resetPasswordApiCall()
            }
        }
    
    //MARK:- FORGOT PASSWORD API
    
    func resetPasswordApiCall(){
        SVProgressHUD.show()
        let parameters: Parameters = [EMAIL :userNameTxtfd.text!,APPID:APPIDVALUE]
        Alamofire.request("\(BASE_URL)\(RESET_PASSWORD_API)", method: .post, parameters: parameters, encoding: JSONEncoding.default, headers: nil).responseObject { (response:DataResponse<User>)  in
            SVProgressHUD.dismiss()
            if Connectivity.isConnectedToInternet() {
                if response.result.value != nil{
                    let json = response.result.value!
                    switch json.code{
                    case "10"?:
                        self.addAlertView(title: json.status!, message: json.msg!, buttonTitle: CLICKOK)
                        break
                    case "0"?:
                        self.addAlertView(title: json.status!, message: json.msg!, buttonTitle: CLICKOK)
                        break
                    case "2"?:
                        self.addAlertView(title: json.status!, message: json.msg!, buttonTitle: CLICKOK)
                        break
                    case .none: break
                    case .some(_): break
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

    // LOGIN BUTTON
    @IBAction func buttonLogin(_ sender: UIButton){
        checkValidations()
        self.view.endEditing(true)
    }
    
    //MARK:- TEXTFIELD DELEGATE
    func textFieldShouldReturn(_ textField: UITextField) -> Bool{
        if(textField == userNameTxtfd){
            passwordTxtfd.becomeFirstResponder()
        }
        else{
            textField.returnKeyType = UIReturnKeyType.done
            textField.resignFirstResponder()
        }
        return false
    }
    
    //MARK:- CUSTOME FUNCTIONS
    func loginApiCall(){
        SVProgressHUD.show()
        let defaults = UserDefaults.standard
        defaults.set(self.userNameTxtfd.text, forKey:EMIALID)
        defaults.set(self.passwordTxtfd.text, forKey:USER_PASSWORD)

        if (UserDefaults.standard.string(forKey: FCM_TOKEN)?.isEmpty) != nil{
            let str = UserDefaults.standard.string(forKey: FCM_TOKEN)
            let parameters: Parameters = [EMAIL :userNameTxtfd.text! , PASSWORD: passwordTxtfd.text!,APPID:APPIDVALUE,"device_token": str!,"device_type": "ios"]
            Alamofire.request("\(BASE_URL)\(LOGIN_API)", method: .post, parameters: parameters, encoding: JSONEncoding.default, headers: nil).responseObject { (response:DataResponse<User>)  in
                SVProgressHUD.dismiss()
                if Connectivity.isConnectedToInternet() {
                    if let json = response.result.value{
                        if response.result.value != nil{
                            switch json.code{
                            case "10":
                                let loginresultDic = json.resultArray?.object(at: 0) as! NSDictionary
                                self.labelAlert.isHidden  = true
                                let accessToken = loginresultDic["accessToken"] as! String
                                let defaults = UserDefaults.standard
                                defaults.set(accessToken, forKey: "accessToken")
                                defaults.set(self.userNameTxtfd.text, forKey:EMIALID)
                                defaults.set(self.passwordTxtfd.text, forKey:USER_PASSWORD)
                                defaults.set(loginresultDic["profile_pic"] as! String, forKey:PROFILE_PIC)
                                let custID = loginresultDic["customer_id"] as! String
                                defaults.set(custID, forKey:CUSTOMER_ID)
//                                let SubscriptionID = loginresultDic["subscription_id"] as! String
//                                defaults.set(SubscriptionID, forKey:SUBSCRIPTION_ID)
                                let userName =  String(format:"%@ %@",loginresultDic["first_name"] as! String,loginresultDic["last_name"] as! CVarArg)
                                defaults.set(userName, forKey:UERS_NAME_COMMENT)
                                let isFollower =  String(format:"%@",loginresultDic["is_follower"] as! CVarArg)
                                defaults.set(isFollower, forKey:IS_FOLLOWER)
                                let password_status = loginresultDic[PASSWORD_STATUS] as! String
                                if password_status == COMPLETED {
                                    let user = Global(json: loginresultDic)
                                    user.saveLoginDetails_IntoUserDefault()
                                    Singleton.sharedInstance.userEmail = self.userNameTxtfd.text!
                                    Singleton.sharedInstance.userpassword = self.passwordTxtfd.text!
                                    self.navigateToFeedScreen()
                                }
                                else if password_status == PENDING {
                                    self.navigateTo_Update_Password_Screen()
                                }else{
                                }
                                SVProgressHUD.dismiss()
                                break
                            case "2":
                                self.labelAlert.isHidden  = false
                                self.buttonSignup.isHidden = false
                                self.buttonSignup.isEnabled = true
                                self.labelAlert.text = "User Email Address/Password is not recognised. Try again or sign up"
                                self.buttonSignup.isUserInteractionEnabled = true
                                guard let text = self.labelAlert.text else { return }
                                let textRange = NSMakeRange(60,7)
                                let attributedText = NSMutableAttributedString(string: text)
                                attributedText.addAttribute(NSAttributedStringKey.underlineStyle , value: NSUnderlineStyle.styleSingle.rawValue, range: textRange)
                                self.labelAlert.attributedText = attributedText
                                self.userNameTxtfd.layer.borderColor = UIColor.init(red: 212/255.0, green: 71/255.0, blue: 71/255.0, alpha: 1.0).cgColor
                                self.userNameTxtfd.textColor = UIColor.init(red: 212/255.0, green: 71/255.0, blue: 71/255.0, alpha: 1.0)
                                self.passwordTxtfd.layer.borderColor = UIColor.init(red: 212/255.0, green: 71/255.0, blue: 71/255.0, alpha: 1.0).cgColor
                                self.passwordTxtfd.textColor = UIColor.init(red: 212/255.0, green: 71/255.0, blue: 71/255.0, alpha: 1.0)
                                SVProgressHUD.dismiss()
                                break
                            case "0":
                                self.addAlertView(title: ALERT, message: json.msg!, buttonTitle: CLICKOK)
                                SVProgressHUD.dismiss()
                                break
                            case "3":
                                self.addAlertView(title: ALERT, message: json.msg!, buttonTitle: CLICKOK)
                                SVProgressHUD.dismiss()
                                break
                            case "4":
                                UserDefaults.standard.set(true, forKey: UPDATE_SUBSCRIPTION_USER)
                                let alert = UIAlertController(title: ALERT, message: json.msg!, preferredStyle: UIAlertControllerStyle.alert)
                                alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: { (action: UIAlertAction!) in
                                    let vc = self.storyboard?.instantiateViewController(withIdentifier: SUBSCRIPTION_PLAN) as! MasterViewController
                                    self.navigationController?.setNavigationBarHidden(true, animated: false)
                                    self.navigationController?.pushViewController(vc, animated: true)
                                }))
                                self.present(alert, animated: true, completion: nil)
                                break
                            case .none: break
                            case .some(_):
                                self.addAlertView(title: ALERT, message:"Internal Server Error", buttonTitle: CLICKOK)
                                SVProgressHUD.dismiss()
                                break
                            }
                        }
                        else{
                            self.addAlertView(title: ALERT, message: SOMESERVERISSUE, buttonTitle: CLICKOK)
                        }
                    }
                    else{
                        self.addAlertView(title: ALERT, message: SOMESERVERISSUE, buttonTitle: CLICKOK)
                    }
                }
                else{
                    self.addAlertView(title: ALERT, message: NOINTERNET, buttonTitle: CLICKOK)
                }
                SVProgressHUD.dismiss()
            }
        }
        else{
            SVProgressHUD.dismiss()
            self.addAlertView(title: ALERT, message: NOINTERNET, buttonTitle: CLICKOK)
        }
    }
       
    func navigateTo_Update_Password_Screen()  {
        let vc=self.storyboard?.instantiateViewController(withIdentifier: UPDATE_PASSWORD_VIEW_CONTROLLER) as! UpdatePasswordViewController
        show(vc, sender: self)
    }
    
    func checkValidations() {
        if userNameTxtfd.text?.isEmpty == true{
            labelAlert.isHidden = false
            labelAlert.text = EMAIL_ALERT
            userNameTxtfd.layer.borderColor = UIColor.red.cgColor
            userNameTxtfd.textColor = UIColor.red
            userNameTxtfd.becomeFirstResponder()
        }
        else if isValidEmail(testStr: userNameTxtfd.text!) == false{
            labelAlert.isHidden = false
            labelAlert.text = VALID_EMAIL_ALERT
            userNameTxtfd.layer.borderColor = UIColor.red.cgColor
            userNameTxtfd.textColor = UIColor.red
            userNameTxtfd.becomeFirstResponder()
        }
        else if passwordTxtfd.text?.isEmpty == true{
            labelAlert.isHidden = false
            labelAlert.text = PASSWORD_ALERT
            userNameTxtfd.layer.borderColor = UIColor.black.cgColor
            userNameTxtfd.textColor = UIColor.black
            passwordTxtfd.layer.borderColor = UIColor.red.cgColor
            passwordTxtfd.textColor = UIColor.red
            passwordTxtfd.becomeFirstResponder()
        }
        else if passwordTxtfd.text!.count < 6 || passwordTxtfd.text!.count > 20{
            labelAlert.isHidden = false
            labelAlert.text = PASSWORD_LENGTH_ALERT
            userNameTxtfd.layer.borderColor = UIColor.black.cgColor
            userNameTxtfd.textColor = UIColor.black
            passwordTxtfd.layer.borderColor = UIColor.red.cgColor
            passwordTxtfd.textColor = UIColor.red
            passwordTxtfd.becomeFirstResponder()
        }
        else{
            labelAlert.isHidden = true
            userNameTxtfd.layer.borderColor = UIColor.black.cgColor
            userNameTxtfd.textColor = UIColor.black
            passwordTxtfd.layer.borderColor = UIColor.black.cgColor
            passwordTxtfd.textColor = UIColor.black
            loginApiCall()
        }
    }
    
    func navigateToFeedScreen(){
        let appDelegate = UIApplication.shared.delegate as! AppDelegate
        appDelegate.feedViewOpen()
    }
    
    @IBAction func signUpAction(_ sender: Any) {
        let vc=self.storyboard?.instantiateViewController(withIdentifier: REGISTRATION_VIEW_CONTROLLER_ONE) as! RegistrationViewControllerOne
        self.navigationController?.pushViewController(vc, animated: true)
    }
}

//MARK:- EXTENSION
    extension UIViewController {
    func hideKeyboardWhenTappedAround() {
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(UIViewController.dismissKeyboard))
        tap.cancelsTouchesInView = false
        view.addGestureRecognizer(tap)
    }
    @objc func dismissKeyboard() {
        view.endEditing(true)
    }
}

