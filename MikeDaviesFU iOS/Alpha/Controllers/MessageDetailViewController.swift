//
//  MessageDetailViewController.swift
//  Alpha
//
//  Created by Monika Tiwari on 15/05/18.
//  Copyright © 2018 Stpl. All rights reserved.
//

import UIKit
import SVProgressHUD
import Alamofire
import MediaPlayer
import AVFoundation
import AVKit

class MessageDetailViewController: UIViewController {
    
    
    var MyObservationContext = 0
    var observing = false
    
    //OUTLET
    
    @IBOutlet var scrollView: UIScrollView!
    @IBOutlet var webView: UIWebView!
    @IBOutlet var webViewHeightConstrain: NSLayoutConstraint!
    
    @IBOutlet weak var fromLbl: UILabel!
    @IBOutlet weak var toLbl: UILabel!
    var descriptionLbl: UITextView!
    @IBOutlet weak var titleLbl: UILabel!
    @IBOutlet weak var imageView: UIImageView!
    @IBOutlet weak var dateLbl: UILabel!
    @IBOutlet weak var playVideoImg: UIImageView!
    
    @IBOutlet var imageBnt: UIButton!
    
    //VARIABLE
    
    var messageArrayData : NSMutableArray = NSMutableArray()
    var messageDetailDictionary = NSDictionary()
    var asset_type:String?
    var from:String?
    var to:String?
    var msg_Title:String?
    var msg_Description:String?
    var date_Time:String?
    var imageUrl : String?
    var thumbUrl : String?
    var messageID:Int?
    var window: UIWindow?
    
    var contentAsset :NSDictionary!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.navigationController?.setNavigationBarHidden(false, animated: false)
        navigationItem.title = APP_NAME
        
        webView.scrollView.isScrollEnabled = false
        
        
        //to remove last separator from tableview
        saveMessageData()
        let tapGestureRecognizerOnImage = UITapGestureRecognizer(target: self, action: #selector(ContentViewController.imageTapped(_:)))
        imageView.isUserInteractionEnabled = true
        playVideoImg.isUserInteractionEnabled = true
        imageView.addGestureRecognizer(tapGestureRecognizerOnImage)
        playVideoImg.addGestureRecognizer(tapGestureRecognizerOnImage)
    }
    
    deinit {
        //  stopObservingHeight()
    }
    
    func startObservingHeight() {
        let options = NSKeyValueObservingOptions([.new])
        webView.scrollView.addObserver(self, forKeyPath: "contentSize", options: options, context: &MyObservationContext)
        observing = true;
    }
    
    func stopObservingHeight() {
        webView.scrollView.removeObserver(self, forKeyPath: "contentSize", context: &MyObservationContext)
        observing = false
    }
    
    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        guard let keyPath = keyPath else {
            super.observeValue(forKeyPath: nil, of: object, change: change, context: context)
            return
        }
        switch keyPath {
        case "contentSize":
            if context == &MyObservationContext {
                webViewHeightConstrain.constant = webView.scrollView.contentSize.height
            }
        default:
            super.observeValue(forKeyPath: keyPath, of: object, change: change, context: context)
        }
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
        
        self.tabBarController?.tabBar.isHidden = true
        
        MessageReadApiCall()
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
    
    @objc func imageTapped(_ sender:UITapGestureRecognizer)
    {
        if asset_type == "document"
        {
            let webView = UIWebView.init(frame: view.frame)
            playVideoImg.isHidden = true
            webView.autoresizingMask = [.flexibleHeight, .flexibleWidth]
            webView.loadRequest(URLRequest.init(url:NSURL.init(string: imageUrl!)! as URL))
            view.addSubview(webView)
        }
        else  if asset_type == "video"
        {
            let videoURL = URL(string: imageUrl!)
            playVideoImg.isHidden = false
            debugPrint("videoURL: \( videoURL!)")
            let player = AVPlayer(url: videoURL!)
            let playerViewController = AVPlayerViewController()
            playerViewController.player = player
            self.present(playerViewController, animated: true) {
                playerViewController.player!.play()
            }
        }
        else  if asset_type == "image"
        {
            let vc=self.storyboard?.instantiateViewController(withIdentifier: "ImageViewController") as! ImageViewController
            playVideoImg.isHidden = true
            vc.checkValue = "messageDetail"
            vc.imageUrl = imageUrl
            self.navigationController?.pushViewController(vc, animated: true)
        }
        
        
        
        
    }
    
    func convertHtml()
    {
        /*  UITextView.appearance().linkTextAttributes = [NSAttributedStringKey.foregroundColor.rawValue: UIColor.blue]
         self.descriptionLbl.text = msg_Description
         descriptionLbl.text =  descriptionLbl.text.replacingOccurrences(of: "tel\"", with: "\"")
         descriptionLbl.text = descriptionLbl.text.replacingOccurrences(of: "email\"", with: "\"")
         descriptionLbl.text = descriptionLbl.text.replacingOccurrences(of: "url\"", with: "\"")
         print("Description string : \(self.descriptionLbl.text)")
         guard let data = descriptionLbl.text?.data(using: String.Encoding.unicode) else { return
         
         }
         
         
         
         try? descriptionLbl.attributedText = NSAttributedString(data: data, options: [.documentType: NSAttributedString.DocumentType.html], documentAttributes: nil)
         self.descriptionLbl.sizeToFit()
         */
        
        print(msg_Description!)
        
        //tel number
        
        if (msg_Description?.contains("tel\">"))!
        {
            var myStringArr = msg_Description?.components(separatedBy: "tel\">")
            
            print("Desc string --->>-\(String(describing: myStringArr![1]))")
            
            var myStringArrNum = myStringArr![1].components(separatedBy: "<")
            
            print("Desc string Number --->>-\(String(describing: myStringArrNum[0]))")
            msg_Description = msg_Description?.replacingOccurrences(of: "href=\"tel\"", with:String(format: "href=tel:%@", myStringArrNum[0] as CVarArg))
        }
        // email
        
        if (msg_Description?.contains("email\">"))!
        {
            var myStringArrEmailStr = msg_Description?.components(separatedBy: "email\">")
            
            print("Desc string --->>-\(String(describing: myStringArrEmailStr![1]))")
            
            var myStringArrEmail = myStringArrEmailStr![1].components(separatedBy: "<")
            
            print("Desc string Email --->>-\(String(describing: myStringArrEmail[0]))")
            
            msg_Description = msg_Description?.replacingOccurrences(of: "href=\"email\"", with:String(format: "href=mailto:%@", myStringArrEmail[0] as CVarArg))
            
        }
        // web site
        
        if (msg_Description?.contains("href=\"url\">"))!
        {
            var myStringArrWebSiteStr = msg_Description?.components(separatedBy: "href=\"url\">")
            
            print("Desc string --->>-\(String(describing: myStringArrWebSiteStr![1]))")
            
            var myStringArrWebsite = myStringArrWebSiteStr![1].components(separatedBy: "<")
            
            print("Desc string Website --->>-\(String(describing: myStringArrWebsite[0]))")
            
            msg_Description = msg_Description?.replacingOccurrences(of: "href=\"url\"", with:String(format: "href=http://%@", myStringArrWebsite[0] as CVarArg))
            
            
        }
        
        msg_Description = msg_Description?.replacingOccurrences(of: "width=", with: " ")
        msg_Description = msg_Description?.replacingOccurrences(of: "height=", with: " ")
        msg_Description = msg_Description?.replacingOccurrences(of: "<iframe", with: "<p align='center'><iframe")
        
        guard (msg_Description?.data(using: String.Encoding.unicode)) != nil else { return }
        
        //        msg_Description =  msg_Description?.replacingOccurrences(of: "tel\"", with: "\"")
        //        msg_Description = msg_Description?.replacingOccurrences(of: "email\"", with: "\"")
        //        msg_Description = msg_Description?.replacingOccurrences(of: "url\"", with: "\"")
        //
        
        webView.mediaPlaybackRequiresUserAction = false
        
        webView.loadHTMLString(msg_Description!,
                               baseURL: nil)
        
        
        
    }
    
    func webView(webView: UIWebView!, shouldStartLoadWithRequest request: NSURLRequest!, navigationType: UIWebViewNavigationType) -> Bool {
        if navigationType == UIWebViewNavigationType.linkClicked {
            if (request.url?.host! == "google.com"){
                return true
            } else {
                UIApplication.shared.openURL(request.url!)
                return false
            }
        }
        return true
    }
    
    @IBAction func imageTapAction(_ sender: Any) {
        
        print ("imaged tap")
        
        if asset_type == "document"
        {
            let webView = UIWebView.init(frame: view.frame)
            playVideoImg.isHidden = true
            webView.autoresizingMask = [.flexibleHeight, .flexibleWidth]
            webView.loadRequest(URLRequest.init(url:NSURL.init(string: imageUrl!)! as URL))
            view.addSubview(webView)
        }
        else  if asset_type == "video"
        {
            let videoURL = URL(string: imageUrl!)
            playVideoImg.isHidden = false
            debugPrint("videoURL: \( videoURL!)")
            let player = AVPlayer(url: videoURL!)
            let playerViewController = AVPlayerViewController()
            playerViewController.player = player
            self.present(playerViewController, animated: true) {
                playerViewController.player!.play()
            }
        }
        else  if asset_type == "image"
        {
            let vc=self.storyboard?.instantiateViewController(withIdentifier: "ImageViewController") as! ImageViewController
            playVideoImg.isHidden = true
            vc.checkValue = "messageDetail"
            vc.imageUrl = imageUrl
            self.navigationController?.pushViewController(vc, animated: true)
        }
        else
        {
            imageBnt.isUserInteractionEnabled = true
        }
        
        
    }
    @objc func messagecellimageTapped(_ sender:UITapGestureRecognizer){
        let vc=self.storyboard?.instantiateViewController(withIdentifier: "ImageViewController") as! ImageViewController
        vc.checkValue = "messageDetail"
        vc.imageUrl = imageUrl
        present(vc, animated: true, completion: nil)
        
    }
    
    @objc func messagecellVideoTapped(_ sender:UITapGestureRecognizer){
        let videoURL = URL(string: imageUrl!)
        debugPrint("videoURL: \( videoURL!)")
        let player = AVPlayer(url: videoURL!)
        let playerViewController = AVPlayerViewController()
        playerViewController.player = player
        self.present(playerViewController, animated: true) {
            playerViewController.player!.play()
        }
    }
    override func viewWillDisappear(_ animated: Bool) {
        tabBarController?.selectedIndex = 3
        self.tabBarController?.tabBar.isHidden = false
    }
    func back(sender: UIBarButtonItem) {
        
        self.tabBarController?.tabBar.isHidden = false
        self.navigationController?.popViewController(animated: true)
    }
    @IBAction func backButton(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
    
    func UTCToLocal(date:String) -> String{
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd hh:mm:ss"
        dateFormatter.timeZone = TimeZone(abbreviation: "UTC")
        
        let dt = dateFormatter.date(from: date)
        dateFormatter.timeZone = TimeZone.current
        dateFormatter.dateFormat = "MM/dd/yyyy @ h:mma"
        return dateFormatter.string(from: dt!)
        
    }
    
    // MARK:- CUSTOM FUNCTIONS
    
    func MessageReadApiCall(){
        print("messageID: \(messageID!)")
        if Connectivity.isConnectedToInternet() {
            let parameters: Parameters = [MESSAGE_ID :String(messageID!)]
            print("parameters: \(parameters)")
            if let userData = UserDefaults.standard.value(forKey: USERDATA) {
                let accessToken = (userData as AnyObject).value(forKey: ACCESS_TOKEN) as! String
                access_Token = accessToken
                print("access_Token: \(access_Token)")
            }
            let headers: HTTPHeaders = [AUTHORIZATION: "\(BEARER)\(access_Token!)"]
            Alamofire.request("\(BASE_URL)\(GET_READ_MESSAGE_API)", method: .post, parameters: parameters, encoding: JSONEncoding.default, headers:headers ).responseJSON { (response) in
                if response.result.value != nil
                {
                    let json = response.result.value! as! NSDictionary
                    print("MessageReadApiCalljsonValue: \(json)")
                    switch json[CODE] as! String
                    {
                    case "10":
                        break
                    case "0":
                        self.addAlertView(title: json[STATUS] as! String, message: json[MESSAGE] as! String, buttonTitle: CLICKOK)
                    case "1":
                        self.addAlertView(title: json[STATUS] as! String, message: json[MESSAGE] as! String, buttonTitle: CLICKOK)
                    default:
                        break
                    }
                }
                else
                {
                    self.addAlertView(title: ALERT, message: SOMESERVERISSUE, buttonTitle: CLICKOK)
                }
            }
        }
        else{
            self.addAlertView(title: ALERT, message: SOMESERVERISSUE, buttonTitle: CLICKOK)
        }
    }
    func saveMessageData(){
        if messageDetailDictionary.count != 0 {
            print("messageDic: \(messageDetailDictionary)")
            
            if messageDetailDictionary[ID] as? Int != nil{
                messageID = messageDetailDictionary[ID] as? Int
                
            }
            else{
                messageID = 0
            }
            if messageDetailDictionary[FROM] as? String != ""{
                from = messageDetailDictionary[FROM] as? String
            }
            else{
                from = ""
            }
            if messageDetailDictionary[TO] as? String != ""{
                to = messageDetailDictionary[TO] as? String
            }
            else{
                to = ""
            }
            if messageDetailDictionary[TITLE] as? String != ""{
                msg_Title = messageDetailDictionary[TITLE] as? String
            }
            else{
                msg_Title = ""
            }
            if messageDetailDictionary[DESCRIPTION] as? String != ""{
                msg_Description = messageDetailDictionary[DESCRIPTION] as? String
            }else{
                msg_Description = ""
            }
            if messageDetailDictionary[CREATED_AT] as? String != ""{
                date_Time = messageDetailDictionary[CREATED_AT] as? String
            }else{
                date_Time = ""
            }
            
            if messageDetailDictionary[ASSET_TYPE] as? String != ""{
                asset_type = messageDetailDictionary[ASSET_TYPE] as? String
            }else{
                asset_type = ""
            }
            if messageDetailDictionary[ATTACHMENT_URL] as? String != nil{
                imageUrl = messageDetailDictionary[ATTACHMENT_URL] as? String
            }else{
                imageUrl = ""
            }
            if messageDetailDictionary[THUMBNAIL_URL] as? String != nil{
                thumbUrl = messageDetailDictionary[THUMBNAIL_URL] as? String
                print("thumbUrlVideo: \(String(describing: thumbUrl!))")
            }
            else{
                thumbUrl = ""
            }
        }
        showMessageData()
    }
    
    func showMessageData(){
        self.toLbl.text = to
        self.fromLbl.text = from
        self.titleLbl.text = msg_Title
        self.dateLbl.text =  date_Time
        //  self.descriptionLbl.text = msg_Description
        //   self.descriptionLbl.isScrollEnabled = false
        //   self.descriptionLbl.sizeToFit()
        if asset_type == "document"
        {
            imageView.sd_setImage(with: URL(string: thumbUrl!), placeholderImage: UIImage(named: ""))
            playVideoImg.isHidden = true
        }
        else  if asset_type == "video"
        {
            imageView.sd_setImage(with: URL(string: thumbUrl!), placeholderImage: UIImage(named: ""))
            playVideoImg.isHidden = false
        }
        else  if asset_type == "image"
        {
            imageView.sd_setImage(with: URL(string: imageUrl!), placeholderImage: UIImage(named: ""))
            playVideoImg.isHidden = true
        }
        else
        {
            imageView.isHidden = true
            playVideoImg.isHidden = true
        }
        convertHtml()
    }
    
}

extension MessageDetailViewController : UIWebViewDelegate {
    
    func webView(_ webView: UIWebView, shouldStartLoadWith request: URLRequest, navigationType: UIWebViewNavigationType) -> Bool {
        
        switch navigationType {
        case .linkClicked:
            // Open links in Safari
            print(request)
            guard let url = request.url else { return true }
            if #available(iOS 10.0, *) {
                print(url)
                UIApplication.shared.open(url, options: [:], completionHandler: nil)
            } else {
                // openURL(_:) is deprecated in iOS 10+.
                print(url)
                UIApplication.shared.openURL(url)
            }
            return false
        default:
            // Handle other navigation types...
            return true
        }
    }
    
    func webViewDidFinishLoad(_ webView: UIWebView) {
        webViewHeightConstrain.constant = webView.scrollView.contentSize.height
        if (!observing) {
            startObservingHeight()
        }
    }
}
