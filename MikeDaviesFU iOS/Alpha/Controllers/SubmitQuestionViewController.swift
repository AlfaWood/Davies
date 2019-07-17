
//
//  SubmitQuestionViewController.swift
//  Alpha
//
//  Created by Monika Tiwari on 08/05/18.
//  Copyright © 2018 Stpl. All rights reserved.
//

import UIKit
import SVProgressHUD
import Alamofire
import AlamofireObjectMapper
import ObjectMapper

class SubmitQuestionViewController: UIViewController,UITextViewDelegate{
    
    @IBOutlet weak var textViewHeight: NSLayoutConstraint!
    var statusUser = "1"
    var sbmtQustnArr = NSArray()
    
    @IBOutlet weak var btnUsernameOutlet: UIButton!
    @IBOutlet weak var btnAnonymousOutlet: UIButton!
    
    @IBOutlet weak var txtDescription: UITextView!
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.txtDescription.becomeFirstResponder()
        
        NotificationCenter.default.addObserver(self, selector: #selector(SubmitQuestionViewController.keyboardWillShow), name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(SubmitQuestionViewController.keyboardWillHide), name: NSNotification.Name.UIKeyboardWillHide, object: nil)
        
        txtDescription.delegate = self
    }
    
    @objc func keyboardWillShow(notification: NSNotification) {
        self.view.frame.origin.y -= 160
    }
    
    @objc func keyboardWillHide(notification: NSNotification) {
        self.view.frame.origin.y = 64 //UPDATED Jagriti
    }
    
    override func viewWillAppear(_ animated: Bool) {
        /*
         let navView = UIView()
         navView.frame =  CGRect(x:-350, y:-10, width:950, height:65)
         let image = UIImageView()
         image.image = UIImage(named: "DPE-Inline")
         image.frame = CGRect(x:-350, y:-10, width:950, height:65)
         navView.sizeToFit()
         image.contentMode = UIViewContentMode.scaleAspectFit
         // Add both the label and image view to the navView
         navView.addSubview(image)
         
         // Set the navigation bar's navigation item's titleView to the navView
         self.navigationItem.titleView = navView
         
         // Set the navView's frame to fit within the titleView
         navView.sizeToFit()
         */
        
        updateNavigationBarUI()
        
        self.navigationController?.setNavigationBarHidden(false, animated: false)
        self.tabBarController?.tabBar.isHidden = true
        
        if UIDevice().userInterfaceIdiom == .phone {
            switch UIScreen.main.bounds.size.height{
            case 480:
                print("iPhone 4S")
            case 568:
                do {
                    print("iPhone 5")
                    //     textViewHeight.constant = 250.0
                }
            default:
                print("other models")
                //         textViewHeight.constant = 380.0
            }
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
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func backButton(_ sender: Any) {
        self.dismiss(animated: true, completion: nil)
    }
    
    @IBAction func btnLogin(_ sender: Any) {
        if let descText = txtDescription.text {
            if descText.isEmpty {
                self.addAlertView(title:ALERT, message:QUESTION_BLANK_ALERT , buttonTitle: CLICKOK)
            } else {
                callForSubmitQuestionsApi()
            }
        }
    }
    
    func textView(_ textView: UITextView, shouldChangeTextIn range: NSRange, replacementText text: String) -> Bool {
        if(text == "\n") {
            textView.resignFirstResponder()
            return false
        }
        let newText = (textView.text as NSString).replacingCharacters(in: range, with: text)
        let numberOfChars = newText.count // for Swift use count(newText)
        
        return numberOfChars <= 1000;
    }
    
    func callForSubmitQuestionsApi()
    {
        //let parameters: Parameters = [ID :"GFG" ]
        if let userData = UserDefaults.standard.value(forKey: USERDATA) {
            let accessToken = (userData as AnyObject).value(forKey: ACCESS_TOKEN) as! String
            access_Token = accessToken
        }
        SVProgressHUD.show()
        let headers: HTTPHeaders = ["Authorization": "\("Bearer ")\(access_Token!)"]
        
        let parameters: Parameters = [QUESTION :self.txtDescription.text! , ANONYMOUS:statusUser]
        Alamofire.request("\(BASE_URL)\(Insert_QA)", method: .post, parameters: parameters, encoding: JSONEncoding.default, headers: headers).responseObject { (response:DataResponse<User>)  in
            SVProgressHUD.dismiss()
            if Connectivity.isConnectedToInternet() {
                if let json = response.result.value
                {
                    if response.result.value != nil
                    {
                        print("code------\(String(describing: json.code))")
                        switch json.code{
                        case "10"?:
                            let alert = UIAlertController(title:json.status, message: json.msg!, preferredStyle: UIAlertControllerStyle.alert)
                            alert.addAction(UIAlertAction(title: "OK", style: .default, handler: { action in
                                switch action.style{
                                case .default:
                                    
                                    print("default")
                                    self.txtDescription.text = ""
                                    
                                case .cancel:
                                    print("cancel")
                                    
                                case .destructive:
                                    print("destructive")
                                    
                                }}))
                            self.present(alert, animated: true, completion: nil)
                            SVProgressHUD.dismiss()
                            break
                        case "0"?:
                            self.addAlertView(title: ALERT, message: json.msg!, buttonTitle: CLICKOK)
                            SVProgressHUD.dismiss()
                            break
                        case "1"?:
                            self.addAlertView(title: ALERT, message: json.msg!, buttonTitle: CLICKOK)
                            SVProgressHUD.dismiss()
                            break
                        case "401"?:
                            Singleton.sharedInstance.refreshToken(arg: true, completion: {(success,jsonCode,jsonMsg) -> Void in
                                print("Second line of code executed")
                                if success { // this will be equal to whatever value is set in this method call
                                    self.callForSubmitQuestionsApi()
                                } else {
                                }
                            }, failure: {(alert,message) -> Void in
                                self.addAlertView(title: alert, message: message, buttonTitle: CLICKOK)
                            }
                            )
                            break
                        case .none: break
                        case .some(_): break
                        }
                    }
                    else{
                        SVProgressHUD.dismiss()
                        //   self.addAlertView(title: ALERT, message: SOMESERVERISSUE, buttonTitle: CLICKOK)
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
    
    @IBAction func btnUserNAme(_ sender: Any) {
        statusUser = "0"
        btnUsernameOutlet.setImage(UIImage(named: "radio_active"), for: UIControlState.normal)
        btnAnonymousOutlet.setImage(UIImage(named: "radio_inactive"), for: UIControlState.normal)
    }
    
    @IBAction func btnAnonymous(_ sender: Any) {
        statusUser = "1"
        btnUsernameOutlet.setImage(UIImage(named: "radio_inactive"), for: UIControlState.normal)
        btnAnonymousOutlet.setImage(UIImage(named: "radio_active"), for: UIControlState.normal)
    }
}
