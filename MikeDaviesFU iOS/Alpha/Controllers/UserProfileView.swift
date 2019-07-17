//
//  UserProfileView.swift
//  Alpha
//
//  Created by Razan Nasir on 6/4/18.
//  Copyright © 2018 Stpl. All rights reserved.
//

import UIKit
import AVFoundation
import AVKit
import Foundation
import SVProgressHUD
import Alamofire
import AlamofireObjectMapper
import ObjectMapper
import Photos

var customerID:String!
var subscriptionID:String!
var isCameraClicked = false

class UserProfileView: UIViewController,UITabBarDelegate,UIImagePickerControllerDelegate,UINavigationControllerDelegate,UITextFieldDelegate{
    
    var userProfileData: NSMutableArray = NSMutableArray()
    var imagePicker = UIImagePickerController()
    var IsComingFrom: String!
    
    let strCard = "Card:"
    
    let activityIndicator:UIActivityIndicatorView = UIActivityIndicatorView();
    
    @IBOutlet var submitbutton_topCons: NSLayoutConstraint!
    @IBOutlet var memberSinceLbl: UILabel!
    @IBOutlet var cardDetails: UILabel!
    
    @IBOutlet weak var imageViewPic: UIImageView!
    var imageView = UIImage()
    var imageView1 = UIImageView()
    
    @IBOutlet weak var btnProfile: UIButton!
    @IBOutlet weak var txtFirstName: UIPaddingExtension?
    
    @IBOutlet weak var txtLastName: UIPaddingExtension?
    
    @IBOutlet weak var txtAlias: UIPaddingExtension?
    
    @IBOutlet weak var txtEmailAddress: UIPaddingExtension?
    
    @IBOutlet weak var middleView: UIView!
    @IBOutlet weak var uperView: UIView!
    
    @IBOutlet weak var bottomView: UIView!
    
    @IBOutlet weak var btnSubmit: UIButton!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.view.addGestureRecognizer(UITapGestureRecognizer(target: self.view, action: #selector(UIView.endEditing(_:))))
        
        imagePicker.delegate = self as UIImagePickerControllerDelegate & UINavigationControllerDelegate
        
        self.gettingCornerRadius()
        self.getUserProfileData()
        
        self.tabBarController?.tabBar.isHidden = true
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        /*
         let navView = UIView()
         
         if UIDevice().userInterfaceIdiom == .phone {
         switch UIScreen.main.bounds.size.height{
         case 480:
         print("iPhone 4S")
         case 568:
         do {
         if(IsComingFrom == "Feed"){
         navView.frame =  CGRect(x:-380, y:-10, width:950, height:65)
         let image = UIImageView()
         image.image = UIImage(named: "nav_logo_white")
         image.frame = CGRect(x:-380, y:-10, width:950, height:65)
         image.contentMode = UIViewContentMode.scaleAspectFit
         navView.sizeToFit()
         // Add both the label and image view to the navView
         navView.addSubview(image)
         
         // Set the navigation bar's navigation item's titleView to the navView
         self.navigationItem.titleView = navView
         }
         else
         {
         navView.frame =  CGRect(x:-350, y:-10, width:950, height:65)
         let image = UIImageView()
         image.image = UIImage(named: "nav_logo_white")
         image.frame = CGRect(x:-350, y:-10, width:950, height:65)
         image.contentMode = UIViewContentMode.scaleAspectFit
         navView.sizeToFit()
         // Add both the label and image view to the navView
         navView.addSubview(image)
         
         // Set the navigation bar's navigation item's titleView to the navView
         self.navigationItem.titleView = navView
         }
         }
         default:
         
         if(IsComingFrom == "Feed"){
         navView.frame =  CGRect(x:-380, y:-10, width:950, height:65)
         let image = UIImageView()
         image.image = UIImage(named: "nav_logo_white")
         image.frame = CGRect(x:-380, y:-10, width:950, height:65)
         image.contentMode = UIViewContentMode.scaleAspectFit
         navView.sizeToFit()
         // Add both the label and image view to the navView
         navView.addSubview(image)
         
         // Set the navigation bar's navigation item's titleView to the navView
         self.navigationItem.titleView = navView
         }
         else
         {
         navView.frame =  CGRect(x:-330, y:-10, width:950, height:65)
         let image = UIImageView()
         image.image = UIImage(named: "nav_logo_white")
         image.frame = CGRect(x:-330, y:-10, width:950, height:65)
         image.contentMode = UIViewContentMode.scaleAspectFit
         navView.sizeToFit()
         // Add both the label and image view to the navView
         navView.addSubview(image)
         
         // Set the navigation bar's navigation item's titleView to the navView
         self.navigationItem.titleView = navView
         }
         print("other models")
         
         }
         }
         
         // Set the navView's frame to fit within the titleView
         navView.sizeToFit()
         */
        
        updateNavigationBarUI()
        
        self.bottomView.isHidden = true
        submitbutton_topCons.constant = 15
        
        //        let isFollower = UserDefaults.standard.value(forKey:IS_FOLLOWER) as! String
        
        /*  if  isFollower == "0"
         {
         self.bottomView.isHidden = true
         submitbutton_topCons.constant = 15
         }
         else
         {
         self.bottomView.isHidden = false
         
         }
         */
        if !isCameraClicked {
            self.getUserProfileData()
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
    
    func getUserProfileData()
    {
        
        let defaults = UserDefaults.standard
        if let accessToken = defaults.string(forKey: "accessToken") {
            access_Token = accessToken
        }
        self.tabBarController?.tabBar.isHidden = true
        
        let headers: HTTPHeaders = ["Authorization": "\("Bearer ")\(access_Token!)"]
        
        Alamofire.request("\(BASE_URL)\(GET_PROFILE)", method: .get, parameters: nil , encoding: JSONEncoding.default, headers:headers ).responseObject { (response:DataResponse<User>)  in
            debugPrint("response: \(response)")
            
            if Connectivity.isConnectedToInternet() {
                if response.result.value != nil
                {
                    let json = response.result.value!
                    switch json.code{
                    case "10"?:
                        self.userProfileData = NSMutableArray(array: json.resultArray!)
                        print("self.UserArrayData: \(self.userProfileData)")
                        for dict in self.userProfileData
                        {
                            let   profileData = (dict as AnyObject).value(forKey: "profile")
                            print(profileData ?? "")
                            
                            self.txtFirstName?.text = (profileData as AnyObject).value(forKey: "first_name") as? String
                            self.txtEmailAddress?.text = (profileData as AnyObject).value(forKey: "email") as? String
                            self.txtAlias?.text = (profileData as AnyObject).value(forKey: "username") as? String
                            self.txtLastName?.text = (profileData as AnyObject).value(forKey: "last_name") as? String
                            
                            let userName =  String(format:"%@ %@",(profileData as AnyObject).value(forKey: "first_name") as! String,(profileData as AnyObject).value(forKey: "last_name") as! CVarArg)
                            defaults.set(userName, forKey:UERS_NAME_COMMENT)
                            
                            self.memberSinceLbl?.text = String (format: "Member Since %@",self.UTCToLocal(date: ((profileData as AnyObject).value(forKey: "created_at") as! String)))
                            
                            /*   let strMonth = (profileData as AnyObject).value(forKey: "exp_month") as! String
                             let strYear = (profileData as AnyObject).value(forKey: "exp_year") as! String
                             let splitStrYear = strYear.suffix(2)
                             let strSlash = "\\"
                             let  strExp  = "\(strMonth)\(strSlash)\(splitStrYear)"
                             */
                            
                            // self.cardDetails.text = String(format: "****  ****  **** %@",((profileData as AnyObject).value(forKey: "last4") as! String))
                            
                            
                            if ((profileData as AnyObject).value(forKey: "last4") as? String ) != nil
                            {
                                
                                let strCardDetails =  String(format: "***  ***  **** %@",((profileData as AnyObject).value(forKey: "last4") as! String))
                                
                                
                                
                                let appendCardDetail = "\(self.strCard) \(strCardDetails)"
                                
                                let string_to_color = self.strCard
                                
                                let range = (appendCardDetail as NSString).range(of: string_to_color)
                                
                                let attribute = NSMutableAttributedString.init(string: appendCardDetail)
                                
                                
                                attribute.addAttribute(NSAttributedStringKey.foregroundColor, value: UIColor(hexString: "212227").withAlphaComponent(1.0), range: range)
                                
                                self.cardDetails.attributedText = attribute
                                
                                
                                
                                
                            }
                            
                            
                            
                            let imgUrl1 = (profileData as AnyObject).value(forKey:"profile_pic") as? String
                            
                            if imgUrl1?.range(of:".png") != nil {
                                self.load_image(image_url_string:imgUrl1!, view:self.imageView1)
                            }
                            else  if imgUrl1?.range(of:".jpg") != nil {
                                self.load_image(image_url_string:imgUrl1!, view:self.imageView1)
                            }
                            else  if imgUrl1?.range(of:".jpeg") != nil {
                                self.load_image(image_url_string:imgUrl1!, view:self.imageView1)
                            }
                            else  if imgUrl1?.range(of:".JPG") != nil {
                                self.load_image(image_url_string:imgUrl1!, view:self.imageView1)
                            }
                            else  if imgUrl1?.range(of:".JPEG") != nil {
                                self.load_image(image_url_string:imgUrl1!, view:self.imageView1)
                            }else  if imgUrl1?.range(of:".PNG") != nil {
                                self.load_image(image_url_string:imgUrl1!, view:self.imageView1)
                            }
                                
                            else
                            {
                                self.load_image(image_url_string:imgUrl1!, view:self.imageView1)
                            }
                            
                        }
                        self.tabBarController?.tabBar.isHidden = true
                        self.navigationController?.setNavigationBarHidden(false, animated: false)
                        
                        let defaults = UserDefaults.standard
                        defaults.set(false, forKey: APP_LAUNCH)
                        
                        break
                    case "0"?:
                        self.addAlertView(title: json.status!, message: json.msg!, buttonTitle: CLICKOK)
                    case "1"?:
                        self.addAlertView(title: json.status!, message: json.msg!, buttonTitle: CLICKOK)
                    case "401"?:
                        Singleton.sharedInstance.refreshToken(arg: true, completion: { (success,jsonCode,jsonMsg) -> Void in
                            if success {
                                if jsonCode == "4"
                                {
                                    let alert = UIAlertController(title: ALERT, message: jsonMsg, preferredStyle: UIAlertControllerStyle.alert)
                                    
                                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: { (action: UIAlertAction!) in
                                        UserDefaults.standard.set(false, forKey: USERLOGGEDIN)
                                        let vc=self.storyboard?.instantiateViewController(withIdentifier: SUBSCRIPTION_PLAN) as! MasterViewController
                                        self.navigationController?.pushViewController(vc, animated: true)
                                        //  let addCardViewController = STPAddCardViewController()
                                        //    addCardViewController.delegate = self
                                        // Present add card view controller
                                        //   self.navigationController?.pushViewController(addCardViewController, animated: true)
                                        
                                        
                                    }))
                                    self.present(alert, animated: true, completion: nil)
                                    self.navigationController?.setNavigationBarHidden(false, animated: false)
                                    self.tabBarController?.tabBar.isHidden = true
                                    
                                }
                                if jsonCode == "3"
                                {
                                    let alert = UIAlertController(title:json.status, message:jsonMsg, preferredStyle: UIAlertControllerStyle.alert)
                                    
                                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: { (action: UIAlertAction!) in
                                        UserDefaults.standard.set(false, forKey: USERLOGGEDIN)
                                        
                                        
                                        let vc=self.storyboard?.instantiateViewController(withIdentifier: LOGIN_VIEW_CONTROLLER) as! LoginViewController
                                        self.navigationController?.pushViewController(vc, animated: true)
                                        
                                    }))
                                    self.present(alert, animated: true, completion: nil)
                                }
                                else if jsonCode == "2"
                                {
                                    let alert = UIAlertController(title:json.status, message:jsonMsg, preferredStyle: UIAlertControllerStyle.alert)
                                    
                                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: { (action: UIAlertAction!) in
                                        UserDefaults.standard.set(false, forKey: USERLOGGEDIN)
                                        
                                        
                                        let vc=self.storyboard?.instantiateViewController(withIdentifier: LOGIN_VIEW_CONTROLLER) as! LoginViewController
                                        self.navigationController?.pushViewController(vc, animated: true)
                                        
                                    }))
                                    self.present(alert, animated: true, completion: nil)
                                }
                                else
                                {
                                    self.getUserProfileData()
                                }
                            } else {
                            }
                        }, failure: {(alert,message) -> Void in
                            self.addAlertView(title: alert, message: message, buttonTitle: CLICKOK)
                        }
                        )
                        break
                    case .none:
                        self.addAlertView(title: json.status!, message: json.msg!, buttonTitle: CLICKOK)
                        break
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
    
    func load_image(image_url_string:String, view:UIImageView)
    {
        
        activityIndicator.center = CGPoint(x: 30, y: 40)
        activityIndicator.hidesWhenStopped = true;
        activityIndicator.activityIndicatorViewStyle = UIActivityIndicatorViewStyle.gray;
        btnProfile.addSubview(activityIndicator);
        
        activityIndicator.startAnimating();
        UIApplication.shared.beginIgnoringInteractionEvents();
        
        let image_url: NSURL = NSURL(string: image_url_string)!
        
        let image_from_url_request: NSURLRequest = NSURLRequest(url: image_url as URL)
        
        UserDefaults.standard.set(image_url_string, forKey:PROFILE_PIC)
        
        NSURLConnection.sendAsynchronousRequest(
            image_from_url_request as URLRequest, queue: OperationQueue.main,
            completionHandler: {(response: URLResponse!,
                data: Data!,
                error: Error!) -> Void in
                
                if error == nil && data != nil {
                    
                    self.activityIndicator.stopAnimating();
                    UIApplication.shared.endIgnoringInteractionEvents();
                    view.image = UIImage(data: data)
                    self.btnProfile.backgroundColor = nil
                    self.btnProfile.layer.cornerRadius =   self.btnProfile.frame.height / 2
                    self.btnProfile.clipsToBounds = true
                    self.btnProfile.setImage(view.image, for: [])
                    self.imageView =  view.image! as UIImage
                    
                }
                else{
                    self.activityIndicator.stopAnimating();
                    UIApplication.shared.endIgnoringInteractionEvents();
                    view.image = UIImage(named: "user_pic.png")
                    self.btnProfile.backgroundColor = nil
                    self.btnProfile.layer.cornerRadius =   self.btnProfile.frame.height / 2
                    self.btnProfile.contentMode = .scaleAspectFit
                    self.btnProfile.clipsToBounds = true
                    self.btnProfile.setImage(view.image, for: [])
                    self.imageView =  view.image! as UIImage
                }
        })
        
        
    }
    
    
    func gettingCornerRadius()
    {
        
        middleView.layer.cornerRadius = 10;
        middleView.layer.masksToBounds = true;
        
        middleView.layer.borderColor = UIColor.gray.cgColor;
        middleView.layer.borderWidth = 1.0;
        
        uperView.layer.cornerRadius = 10;
        uperView.layer.masksToBounds = true;
        
        uperView.layer.borderColor = UIColor.gray.cgColor;
        uperView.layer.borderWidth = 1.0;
        
        
        bottomView.layer.cornerRadius = 10;
        bottomView.layer.masksToBounds = true;
        
        //  bottomView.layer.borderColor = UIColor.bt_color(fromHex: "1C7B9B", alpha: 1.0).cgColor;
        bottomView.layer.borderColor = UIColor(hexString: "#1C7B9B").withAlphaComponent(1.0).cgColor ;
        bottomView.layer.borderWidth = 1.0;
        
        btnSubmit.layer.cornerRadius = 3;
        btnSubmit.layer.masksToBounds = true;
        
        
        
        
    }
    
    
    @IBAction func buttonClicked(sender:UIButton)
    {
        // Setup add card view controller
        
        isCameraClicked = false
        let alertController = UIAlertController(title:"",
                                                message:ADD_CARD_ALERT,
                                                preferredStyle: .alert)
        
        let alertActioncancel = UIAlertAction(title: "NO", style: .default, handler: { _ in
        })
        
        
        
        let alertAction = UIAlertAction(title: "YES", style: .default, handler: { _ in
            self.navigationController?.navigationBar.tintColor = tabTintColor
            // Present add card view controller
            alertController.addAction(alertActioncancel)
            self.present(alertController, animated: true)
        })
    }
    
    @IBAction func buttonCancelAction(_ sender: Any) {
        let alertController = UIAlertController(title: "",
                                                message: CANCEL_CARD_ALERT,
                                                preferredStyle: .alert)
        
        let alertActioncancel = UIAlertAction(title: "NO", style: .default, handler: { _ in
        })
        
        let alertAction = UIAlertAction(title: "YES", style: .default, handler: { _ in
            self.cancelMembership()
        })
        
        alertController.addAction(alertActioncancel)
        alertController.addAction(alertAction)
        self.present(alertController, animated: true)
    }
    
    @IBAction func buttonClickedDelete(sender:UIButton)
    {
        let vc = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "EditCardDetailsView") as! EditCardDetailsView
        navigationController?.pushViewController(vc,animated: true)
    }
    
    // MARK: STPAddCardViewControllerDelegate
    
    func UTCToLocal(date:String) -> String {
        
        //Code added by Chandramouli
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
        let date = dateFormatter.date(from: date)
        dateFormatter.dateFormat = "MMMM dd, yyyy"
        return  dateFormatter.string(from: date!)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func btnChangePwd(_ sender: Any) {
        
        let vc = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: "UpdatePasswordViewController") as! UpdatePasswordViewController
        navigationController?.pushViewController(vc,animated: true)
    }
    
    
    @IBAction func btnProfilePic(_ sender: Any) {
        
        let vc=self.storyboard?.instantiateViewController(withIdentifier: "UserImageView") as! UserImageView
        
        
        vc.userImage = imageView
        
        self.navigationController?.pushViewController(vc, animated: true)
    }
    
    @IBAction func btnCamera(_ sender: Any) {
        
        isCameraClicked = true
        let alert = UIAlertController(title: "Choose Image", message: nil, preferredStyle: .actionSheet)
        alert.addAction(UIAlertAction(title: "Camera", style: .default, handler: { _ in
            self.openCamera()
        }))
        
        alert.addAction(UIAlertAction(title: "Gallery", style: .default, handler: { _ in
            self.openGallary()
        }))
        
        alert.addAction(UIAlertAction.init(title: "Cancel", style: .cancel, handler: nil))
        
        /*If you want work actionsheet on ipad
         then you have to use popoverPresentationController to present the actionsheet,
         otherwise app will crash on iPad */
        
        switch UIDevice.current.userInterfaceIdiom {
        case .pad:
            alert.popoverPresentationController?.sourceView = sender as? UIView
            alert.popoverPresentationController?.sourceRect = (sender as AnyObject).bounds
            alert.popoverPresentationController?.permittedArrowDirections = .up
        default:
            break
        }
        
        self.present(alert, animated: true, completion: nil)
    }
    
    func openCamera()
    {
        if(UIImagePickerController .isSourceTypeAvailable(UIImagePickerControllerSourceType.camera))
        {
            
            imagePicker.sourceType = UIImagePickerControllerSourceType.camera
            imagePicker.allowsEditing = true
            imagePicker.delegate = self as UIImagePickerControllerDelegate & UINavigationControllerDelegate
            self.present(imagePicker, animated: true, completion: nil)
        }
        else
        {
            let alert  = UIAlertController(title: "Warning", message: "You don't have camera", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "OK", style: .default, handler: nil))
            self.present(alert, animated: true, completion: nil)
        }
    }
    
    func openGallary()
    {
        PHPhotoLibrary.requestAuthorization({(status:PHAuthorizationStatus)in
            switch status{
            case .denied:
                break
            case .authorized:
                break
            default:
                break
            }
        })
        imagePicker.sourceType = UIImagePickerControllerSourceType.photoLibrary
        imagePicker.allowsEditing = true
        imagePicker.delegate = self as UIImagePickerControllerDelegate & UINavigationControllerDelegate
        self.present(imagePicker, animated: true, completion: nil)
    }
    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : Any]) {
        if let pickedImage = info[UIImagePickerControllerEditedImage] as? UIImage {
            self.btnProfile.layer.cornerRadius =   self.btnProfile.frame.height / 2
            self.btnProfile.clipsToBounds = true
            
            imageView = fixOrientation(img: pickedImage)
            
            btnProfile.setImage(imageView , for: [])
            
        }
        picker.dismiss(animated: true, completion: nil)
    }
    func fixOrientation(img: UIImage) -> UIImage {
        if (img.imageOrientation == .up) {
            return img
        }
        
        UIGraphicsBeginImageContextWithOptions(img.size, false, img.scale)
        let rect = CGRect(x: 0, y: 0, width: img.size.width, height: img.size.height)
        img.draw(in: rect)
        
        let normalizedImage = UIGraphicsGetImageFromCurrentImageContext()!
        UIGraphicsEndImageContext()
        
        return normalizedImage
    }
    
    @IBAction func btnSubmit1(_ sender: Any)
    {
        submitProfile()
    }
    
    func submitProfile()
    {
        SVProgressHUD.show()
        let defaults = UserDefaults.standard
        if let accessToken = defaults.string(forKey: "accessToken") {
            access_Token = accessToken
        }
        self.tabBarController?.tabBar.isHidden = true
        
        let imageData:NSData = UIImageJPEGRepresentation(imageView, 0.0)! as NSData
        let strBase64 = imageData.base64EncodedString(options: .lineLength64Characters)
        let headers: HTTPHeaders = ["Authorization": "\("Bearer ")\(access_Token!)"]
        let parameters1: Parameters = ["first_name" :self.txtFirstName!.text!,"last_name":self.txtLastName!.text!,"username" : self.txtAlias!.text!,"profile_pic" :strBase64]
        print(parameters1)
        
        Alamofire.request("\(BASE_URL)\(UPDATE_PROFILE)", method: .post, parameters: parameters1 , encoding: JSONEncoding.default, headers:headers ).responseObject { (response:DataResponse<User>)  in
            debugPrint("response: \(response)")
            if Connectivity.isConnectedToInternet() {
                if response.result.value != nil
                {
                    SVProgressHUD.dismiss()
                    let json = response.result.value!
                    switch json.code{
                    case "10"?:
                        print(response)
                        self.addAlertView(title: json.status!, message: json.msg!, buttonTitle: CLICKOK)
                        if (json.resultArray?.count != 0)
                        {
                            let updateProfileResultDic = json.resultArray?.object(at: 0) as! NSDictionary
                            let UserDict = updateProfileResultDic["updated_profile"] as! NSDictionary
                            defaults.set(UserDict["profile_pic"] as! String, forKey:PROFILE_PIC)
                        }
                        break
                    case "0"?:
                        self.addAlertView(title: ALERT, message: json.msg!, buttonTitle: CLICKOK)
                    case "1"?:
                        self.addAlertView(title: ALERT, message: json.msg!, buttonTitle: CLICKOK)
                    case "401"?:
                        Singleton.sharedInstance.refreshToken(arg: true, completion: {(success,jsonCode,jsonMsg) -> Void in
                            print("Second line of code executed")
                            if success {
                                if jsonCode == "4"
                                {
                                    let alert = UIAlertController(title: ALERT, message: jsonMsg, preferredStyle: UIAlertControllerStyle.alert)
                                    
                                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: { (action: UIAlertAction!) in
                                        
                                    }))
                                    self.present(alert, animated: true, completion: nil)
                                    self.navigationController?.setNavigationBarHidden(false, animated: false)
                                    self.tabBarController?.tabBar.isHidden = true
                                    
                                }
                                else if jsonCode == "3"
                                {
                                    let alert = UIAlertController(title:json.status, message:jsonMsg, preferredStyle: UIAlertControllerStyle.alert)
                                    
                                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: { (action: UIAlertAction!) in
                                        UserDefaults.standard.set(false, forKey: USERLOGGEDIN)
                                        
                                        
                                        let vc=self.storyboard?.instantiateViewController(withIdentifier: LOGIN_VIEW_CONTROLLER) as! LoginViewController
                                        self.navigationController?.pushViewController(vc, animated: true)
                                        
                                    }))
                                    self.present(alert, animated: true, completion: nil)
                                }
                                else if jsonCode == "2"
                                {
                                    let alert = UIAlertController(title:json.status, message:jsonMsg, preferredStyle: UIAlertControllerStyle.alert)
                                    
                                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: { (action: UIAlertAction!) in
                                        UserDefaults.standard.set(false, forKey: USERLOGGEDIN)
                                        
                                        
                                        let vc=self.storyboard?.instantiateViewController(withIdentifier: LOGIN_VIEW_CONTROLLER) as! LoginViewController
                                        self.navigationController?.pushViewController(vc, animated: true)
                                        
                                    }))
                                    self.present(alert, animated: true, completion: nil)
                                }
                                else
                                {
                                    self.submitProfile()
                                }
                            } else {
                            }
                        }, failure: {(alert,message) -> Void in
                            self.addAlertView(title: alert, message: message, buttonTitle: CLICKOK)
                        }
                        )
                        break
                    case .none:
                        Singleton.sharedInstance.refreshToken(arg: true, completion: {(success,jsonCode,jsonMsg) -> Void in
                            print("Second line of code executed")
                            if success { // this will be equal to whatever value is set in this method call
                                self.submitProfile()
                            } else {
                            }
                        }, failure: {(alert,message) -> Void in
                            self.addAlertView(title: alert, message: message, buttonTitle: CLICKOK)
                        }
                        )
                        break
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
    
    func cancelMembership()
    {
        SVProgressHUD.show()
        let defaults = UserDefaults.standard
        if let accessToken = defaults.string(forKey: "accessToken") {
            access_Token = accessToken
        }
        
        if let customer_id = defaults.string(forKey: CUSTOMER_ID) {
            customerID = customer_id
        }
        
        if let subscription_id = defaults.string(forKey: SUBSCRIPTION_ID) {
            subscriptionID = subscription_id
        }
        
        let headers: HTTPHeaders = ["Authorization": "\("Bearer ")\(access_Token!)"]
        let parameters: Parameters = ["subscription_id" :subscriptionID,"customer_id":customerID]
        print(parameters)
        
        Alamofire.request("\(BASE_URL)\(CANCEL_MEMBERSHIP)", method: .post, parameters: parameters , encoding: JSONEncoding.default, headers:headers ).responseObject { (response:DataResponse<User>)  in
            debugPrint("response: \(response)")
            if Connectivity.isConnectedToInternet() {
                if response.result.value != nil
                {
                    SVProgressHUD.dismiss()
                    let json = response.result.value!
                    switch json.code{
                    case "10"?:
                        print(response)
                        let vc = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: LOGIN_VIEW_CONTROLLER) as! LoginViewController
                        self.navigationController?.pushViewController(vc,animated: true)
                        break
                    case "0"?:
                        self.addAlertView(title: ALERT, message: json.msg!, buttonTitle: CLICKOK)
                    case "1"?:
                        self.addAlertView(title: ALERT, message: json.msg!, buttonTitle: CLICKOK)
                    case "401"?:
                        Singleton.sharedInstance.refreshToken(arg: true, completion: {(success,jsonCode,jsonMsg) -> Void in
                            print("Second line of code executed")
                            if success {
                                if jsonCode == "4"
                                {
                                    let alert = UIAlertController(title: ALERT, message: jsonMsg, preferredStyle: UIAlertControllerStyle.alert)
                                    
                                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: { (action: UIAlertAction!) in
                                    }))
                                    self.present(alert, animated: true, completion: nil)
                                    self.navigationController?.setNavigationBarHidden(false, animated: false)
                                    self.tabBarController?.tabBar.isHidden = true
                                    
                                }
                                else if jsonCode == "3"
                                {
                                    let alert = UIAlertController(title:json.status, message:jsonMsg, preferredStyle: UIAlertControllerStyle.alert)
                                    
                                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: { (action: UIAlertAction!) in
                                        UserDefaults.standard.set(false, forKey: USERLOGGEDIN)
                                        
                                        
                                        let vc=self.storyboard?.instantiateViewController(withIdentifier: LOGIN_VIEW_CONTROLLER) as! LoginViewController
                                        self.navigationController?.pushViewController(vc, animated: true)
                                        
                                    }))
                                    self.present(alert, animated: true, completion: nil)
                                }
                                else if jsonCode == "2"
                                {
                                    let alert = UIAlertController(title:json.status, message:jsonMsg, preferredStyle: UIAlertControllerStyle.alert)
                                    
                                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: { (action: UIAlertAction!) in
                                        UserDefaults.standard.set(false, forKey: USERLOGGEDIN)
                                        
                                        
                                        let vc=self.storyboard?.instantiateViewController(withIdentifier: LOGIN_VIEW_CONTROLLER) as! LoginViewController
                                        self.navigationController?.pushViewController(vc, animated: true)
                                        
                                    }))
                                    self.present(alert, animated: true, completion: nil)
                                }
                                else
                                {
                                    self.cancelMembership()
                                }
                            } else {
                            }
                        }, failure: {(alert,message) -> Void in
                            self.addAlertView(title: alert, message: message, buttonTitle: CLICKOK)
                        }
                        )
                        break
                    case .none:
                        Singleton.sharedInstance.refreshToken(arg: true, completion: {(success,jsonCode,jsonMsg) -> Void in
                            print("Second line of code executed")
                            if success { // this will be equal to whatever value is set in this method call
                                self.cancelMembership()
                            } else {
                            }
                        }, failure: {(alert,message) -> Void in
                            self.addAlertView(title: alert, message: message, buttonTitle: CLICKOK)
                        }
                        )
                        break
                    case .some(_):
                        self.addAlertView(title: ALERT, message: json.msg!, buttonTitle: CLICKOK)
                        break
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
    
    //MARK:- TEXTFIELD DELEGATE
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool
    {
        if(textField == txtFirstName)
        {
            txtLastName!.becomeFirstResponder()
        }
        else if(textField == txtLastName)
        {
            txtAlias!.becomeFirstResponder()
        }
        else
        {
            textField.returnKeyType = UIReturnKeyType.done
            textField.resignFirstResponder()
        }
        return false
    }
    
}
