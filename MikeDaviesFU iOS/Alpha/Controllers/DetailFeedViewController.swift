//
//  DetailFeedViewController.swift
//  Alpha
//
//  Created by Razan Nasir on 11/04/18.
//  Copyright © 2018 Stpl. All rights reserved.
//

import UIKit

import MediaPlayer
import AVFoundation
import AVKit
import SVProgressHUD
import Alamofire
import AlamofireObjectMapper
import ObjectMapper
import youtube_ios_player_helper

var MyObservationContext = 0

class DetailFeedViewController: UIViewController , UITableViewDelegate, UITableViewDataSource,GrowingTextViewDelegate
{
    
    var observing = false
    
    @IBOutlet var heightConstarints: NSLayoutConstraint!
    @IBOutlet var heartBtn: UIButton!
    
    @IBOutlet var webView: UIWebView!
    
    @IBOutlet var webViewHeightConstrain: NSLayoutConstraint!
    @IBOutlet weak var scrollView: UIScrollView!
    
    @IBOutlet weak var tableView: UITableView!
    
    @IBOutlet weak var txtComment: UITextView!
    
    @IBOutlet weak var lblConstraint: NSLayoutConstraint!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var dateTimeLbl: UILabel!
    @IBOutlet weak var feedTypeLbl: UILabel!
    @IBOutlet weak var descriptionLbl: UITextView!
    @IBOutlet weak var commentLabel: UILabel!
    @IBOutlet weak var likeLabel: UILabel!
    @IBOutlet weak var imageView: UIImageView!
    @IBOutlet weak var upperView: UIView!
    @IBOutlet weak var commentLbl: UILabel!
    @IBOutlet weak var likeLbl: UILabel!
    @IBOutlet weak var commentBtn: UIButton!
    @IBOutlet weak var likeBtn: UIButton!
    @IBOutlet weak var lowerLine: UIView!
    
    @IBOutlet weak var imgPlayVideo: UIImageView!
    
    
    
    var dict = NSDictionary()
    
    var strComment = ""
    
    var feed_id = ""
    var pageNo = ""
    var feed_unique_Id = ""
    
    var CommentBy  = ""
    var  ByName = ""
    var FullComment = ""
    
    var IsBtnClicked = ""
    
    var detailDictionary :NSDictionary!
    
    var kbHeight: CGFloat!
    
    var likeUsers  = Int()
    
    var likeArray = NSMutableArray()
    
    
    var arrComment = NSMutableArray()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let color = UIColor.black.cgColor
        txtComment.layer.borderColor = color
        txtComment.layer.borderWidth = 1.0
        txtComment.layer.cornerRadius = 5
        
        
        
        webView.dataDetectorTypes = UIDataDetectorTypes.all
        
        // myWebView.dataDetectorTypes = UIDataDetectorTypeAll
        
        self.txtComment.contentInset = UIEdgeInsetsMake(5, 5, 5, 35);
        
        //  txtComment.contentEdgeInsets = UIEdgeInsets(top: 2, left: 10, bottom: 2, right: 10)
        
        NotificationCenter.default.addObserver(self, selector: #selector(DetailFeedViewController.keyboardWillShow), name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(DetailFeedViewController.keyboardWillHide), name: NSNotification.Name.UIKeyboardWillHide, object: nil)
        
        
        
        self.navigationController?.setNavigationBarHidden(false, animated: false)
        
        print(detailDictionary)
        
        likeUsers = (detailDictionary[CURRENT_USER_LIKE_POST] as? Int)!
        
        let tapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(DetailFeedViewController.imageTapped(_:)))
        imageView?.isUserInteractionEnabled = true
        imageView?.addGestureRecognizer(tapGestureRecognizer)
        showDetailData()
        tableView.estimatedRowHeight = 5.0
        tableView.rowHeight = UITableViewAutomaticDimension
        let tapGesture =  UITapGestureRecognizer(target: self, action: #selector(DetailFeedViewController.hideKeyboard))
        tapGesture.cancelsTouchesInView = true
        view.addGestureRecognizer(tapGesture)
        
        webView.scrollView.isScrollEnabled = false
        webView.delegate = self
        /*
         if let url = URL(string: "https://www.google.de/intl/de/policies/terms/regional.html") {
         webView.loadRequest(URLRequest(url: url))
         }
         */
        
    }
    
    deinit {
        // stopObservingHeight()
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
    
    @objc func hideKeyboard() {
        tableView.endEditing(true)
        txtComment.resignFirstResponder()
    }
    
    @objc func keyboardWillShow(notification: NSNotification) {
        if let keyboardSize = (notification.userInfo?[UIKeyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue {
            if self.view.frame.origin.y == 0 || self.view.frame.origin.y == 64 {
                self.view.frame.origin.y -= keyboardSize.height
            }
        }
    }
    
    @objc func keyboardWillHide(notification: NSNotification) {
        if let keyboardSize = (notification.userInfo?[UIKeyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue {
            if self.view.frame.origin.y != 0 || self.view.frame.origin.y != 64 {
                self.view.frame.origin.y += keyboardSize.height
            }
        }
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        // this will hide the keyboard
        textField.resignFirstResponder()
        return true
    }
    
    @IBAction func btnHeart(_ sender: Any) {
        
        self.beginAnimation(imgView: (likeBtn.imageView)!)
        
        heartBtn.isUserInteractionEnabled = false
        
        if likeUsers == 0
        {
            likeUsers = likeUsers + 1
            
            IsBtnClicked = "1"
            
            likeBtn.setImage(UIImage(named: "Heart"), for: UIControlState.normal)
        }
        else
        {
            IsBtnClicked = "0"
            
            likeUsers = likeUsers - 1
            likeBtn.setImage(UIImage(named: "Heart_line"), for: UIControlState.normal)
        }
        
        feedID = String(format: "%@", detailDictionary[FEED_ID] as! CVarArg)
        feed_unique_id = String(format: "%@", detailDictionary[FEEd_Unique_ID] as! CVarArg)
        print("\(feedID)\n\(feed_unique_id)")
        self.callforLike()
    }
    
    func beginAnimation (imgView : UIImageView) {
        UIView.animate(withDuration: 0.6, delay:0, options: [.repeat, .autoreverse], animations: {
            UIView.setAnimationRepeatCount(1)
            imgView.transform = CGAffineTransform(scaleX: 1.5, y: 1.5)
        }, completion: {completion in
            imgView.transform = CGAffineTransform(scaleX: 1, y: 1)
            
        })
        
    }
    
    func  callforLike()
    {
        if let userData = UserDefaults.standard.value(forKey: USERDATA) {
            let accessToken = (userData as AnyObject).value(forKey: ACCESS_TOKEN) as! String
            access_Token = accessToken
        }
        let parameters : Parameters = ["feed_id":feedID,"feed_unique_id":feed_unique_id,"like":IsBtnClicked]
        
        let headers: HTTPHeaders = ["Authorization": "\("Bearer ")\(access_Token!)"]
        Alamofire.request("\(BASE_URL)\(LIKE_API)", method: .post, parameters: parameters , encoding: JSONEncoding.default, headers:headers ).responseObject { (response:DataResponse<User>)  in
            SVProgressHUD.dismiss()
            
            print("response: \(response)")
            if Connectivity.isConnectedToInternet() {
                if response.result.value != nil
                {
                    self.heartBtn.isUserInteractionEnabled = true
                    self.view.alpha = 1.0
                    let json = response.result.value!
                    switch json.code{
                    case "10"?:
                        if let userLikes = json.totalLikes
                        {
                            self.likeLabel.text = "\(String(userLikes))"
                        }
                        break
                    case "0"?:
                        self.addAlertView(title: json.status!, message: json.msg!, buttonTitle: CLICKOK)
                    case "1"?:
                        self.addAlertView(title: json.status!, message: json.msg!, buttonTitle: CLICKOK)
                    case "401"?:
                        Singleton.sharedInstance.refreshToken(arg: true, completion: { (success,jsonCode,jsonMsg) -> Void in
                            print("Second line of code executed")
                            if success { // this will be equal to whatever value is set in this method call
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
    
    override func viewWillAppear(_ animated: Bool) {
        
        self.tabBarController?.tabBar.isHidden = true
        
        feed_id =  String(format: "%@",detailDictionary[FEED_ID] as! CVarArg)
        
        feed_unique_Id = String(format: "%@",detailDictionary[FEEd_Unique_ID] as! CVarArg)
        
        pageNo = "1"
        
        self.callForComments()
        
        
        self.tabBarController?.tabBar.isHidden = true
        
        let headerView: UIView = UIView.init(frame: CGRect(x: 1, y: 50, width: 276, height: 30))
        headerView.backgroundColor = .white
        
        /*
         let navView = UIView()
         
         if UIDevice().userInterfaceIdiom == .phone {
         switch UIScreen.main.bounds.size.height{
         case 480:
         print("iPhone 4S")
         case 568:
         do {
         
         navView.frame =  CGRect(x:-380, y:-10, width:950, height:65)
         let image = UIImageView()
         image.image = UIImage(named: "DPE-Inline")
         image.frame = CGRect(x:-380, y:-10, width:950, height:65)
         // To maintain the image's aspect ratio:
         // Setting the image frame so that it's immediately before the text:
         navView.sizeToFit()
         image.contentMode = UIViewContentMode.scaleAspectFit
         // Add both the label and image view to the navView
         navView.addSubview(image)
         
         // Set the navigation bar's navigation item's titleView to the navView
         self.navigationItem.titleView = navView
         }
         default:
         
         navView.frame =  CGRect(x:-380, y:-10, width:950, height:65)
         let image = UIImageView()
         image.image = UIImage(named: "DPE-Inline")
         image.frame = CGRect(x:-380, y:-10, width:950, height:65)
         navView.sizeToFit()
         // To maintain the image's aspect ratio:
         // Setting the image frame so that it's immediately before the text:
         
         image.contentMode = UIViewContentMode.scaleAspectFit
         
         // Add both the label and image view to the navView
         navView.addSubview(image)
         
         // Set the navigation bar's navigation item's titleView to the navView
         self.navigationItem.titleView = navView
         
         print("other models")
         
         }
         }
         
         navView.sizeToFit()
         */
        
        updateNavigationBarUI()
        
        if detailDictionary[ALPHA_COMMENTED] as? Int != nil {
            let alphaCommented = detailDictionary[ALPHA_COMMENTED] as? Int
            if alphaCommented == 0
            {
                self.tableView.tableHeaderView?.isHidden = true
            }
            else
            {
                let labelView: UILabel = UILabel.init(frame: CGRect(x: 4, y: 5, width: 276, height: 24))
                labelView.text = detailDictionary[ALPHA_NAME] as? String
                labelView.font = UIFont(name: "HelveticaNeue-Italic", size: 14)!
                labelView.textColor = tabTintColor
                headerView.addSubview(labelView)
                self.tableView.tableHeaderView = headerView
            }
        }
        imageView.contentMode = UIViewContentMode.scaleAspectFit;
        webView.dataDetectorTypes=UIDataDetectorTypes.all;
        txtComment.resignFirstResponder()
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
    
    override func viewWillDisappear(_ animated: Bool) {
        
    }
    
    func showDetailData(){
        
        print("detailDictionary: \(detailDictionary!)")
        let dateTime = detailDictionary[DATE_TIME] as? String
        dateTimeLbl.text =   dateTime
        feedTypeLbl.text = detailDictionary[FEED_TYPE] as? String
        
        //  descriptionLbl.text = detailDictionary[STORYDATA_HTML] as? String
        
        // descriptionLbl.backgroundColor = UIColor.green
        
        var likeCount:Int? = Int((likeLabel.text)!)
        
        if detailDictionary[STORYDATA_HTML] as? String != nil{
            
            var descString = detailDictionary[STORYDATA_HTML] as? String
            
            //          var descString = "<html><body link=\"#9B9B9B\" style=\"word-wrap: break-word;font-family: Helvetica;font-size: 18px;\"><p>Demo post in feeds.</p><p><a href=\"tel\">12234556789</a></p><p><a href=\"email\">Alpha@iaaa.com</a></p><p><a href=\"url\">www.google.com</a></p></body></html>"
            
            
            //tel number
            
            if (descString?.contains("tel\">"))!
            {
                var myStringArr = descString?.components(separatedBy: "tel\">")
                
                print("Desc string --->>-\(String(describing: myStringArr![1]))")
                
                var myStringArrNum = myStringArr![1].components(separatedBy: "<")
                
                print("Desc string Number --->>-\(String(describing: myStringArrNum[0]))")
                descString = descString?.replacingOccurrences(of: "href=\"tel\"", with:String(format: "href=tel:%@", myStringArrNum[0] as CVarArg))
            }
            // email
            
            if (descString?.contains("email\">"))!
            {
                var myStringArrEmailStr = descString?.components(separatedBy: "email\">")
                
                print("Desc string --->>-\(String(describing: myStringArrEmailStr![1]))")
                
                var myStringArrEmail = myStringArrEmailStr![1].components(separatedBy: "<")
                
                print("Desc string Email --->>-\(String(describing: myStringArrEmail[0]))")
                
                descString = descString?.replacingOccurrences(of: "href=\"email\"", with:String(format: "href=mailto:%@", myStringArrEmail[0] as CVarArg))
                
            }
            // web site
            
            if (descString?.contains("href=\"url\">"))!
            {
                var myStringArrWebSiteStr = descString?.components(separatedBy: "href=\"url\">")
                
                print("Desc string --->>-\(String(describing: myStringArrWebSiteStr![1]))")
                
                var myStringArrWebsite = myStringArrWebSiteStr![1].components(separatedBy: "<")
                
                print("Desc string Website --->>-\(String(describing: myStringArrWebsite[0]))")
                
                descString = descString?.replacingOccurrences(of: "href=\"url\"", with:String(format: "href=http://%@", myStringArrWebsite[0] as CVarArg))
                
                
            }
            
            descString = descString?.replacingOccurrences(of: "width=", with: " ")
            descString = descString?.replacingOccurrences(of: "height=", with: " ")
            descString = descString?.replacingOccurrences(of: "<iframe", with: "<p align='center'><iframe")
            
            print("Desc string after replace--->>-\(descString)")
            
            //            descString =  descString.replacingOccurrences(of: "tel\"", with: "\"")
            //            descString = descString.replacingOccurrences(of: "email\"", with: "\"")
            //            descString = descString.replacingOccurrences(of: "url\"", with: "\"")
            
            
            //UIScreen.main.bounds.width
            //Find <iframe width=\"853\" .... replace with <iframe width=\"screen_width\"
            
            /*  UITextView.appearance().linkTextAttributes = [NSAttributedStringKey.foregroundColor.rawValue: UIColor.blue]
             
             descriptionLbl.text =  descriptionLbl.text.replacingOccurrences(of: "tel\"", with: "\"")
             descriptionLbl.text = descriptionLbl.text.replacingOccurrences(of: "email\"", with: "\"")
             descriptionLbl.text = descriptionLbl.text.replacingOccurrences(of: "url\"", with: "\"")
             
             print("Description string : \(descriptionLbl.text)")
             
             
             
             //guard let data = descriptionLbl.text?.data(using: String.Encoding.unicode) else { return }
             
             guard let data = descString?.data(using: String.Encoding.unicode) else { return }
             
             try? descriptionLbl.attributedText = NSAttributedString(data: data, options: [.documentType: NSAttributedString.DocumentType.html], documentAttributes: nil)
             
             adjustUITextViewHeight(arg: descriptionLbl)
             
             
             let webV:UIWebView = UIWebView(frame:CGRect(x :descriptionLbl.bounds.origin.x, y : descriptionLbl.bounds.origin.y, width : descriptionLbl.bounds.width, height : descriptionLbl.bounds.height+15))
             webV.delegate = self
             
             webV.scrollView.bounces = false
             //            webV.scrollView.isScrollEnabled = false
             
             //            webV.contentMode = UIViewContentMode.scaleAspectFill
             //            webV.scalesPageToFit = true
             descriptionLbl.addSubview(webV)
             webV.loadHTMLString(descString!,
             baseURL: nil)
             
             */
            if detailDictionary[ALPHA_COMMENTED] as? Int != nil {
                descString = String(format: "<html><body style=\"word-wrap: break-word;font-family: Helvetica;font-size: 16px;\"><p>%@</p></body></html>",descString!)
            }
            webView.mediaPlaybackRequiresUserAction = false
            
            webView.loadHTMLString(descString!,
                                   baseURL:NSURL(string:"http://")! as URL)
            
        }
        
        if let likeInt = (detailDictionary[LIKE] as? Int){
            if likeInt != 0{
                if likeInt == 1{
                    likeLabel.text  = "\(String(likeInt))"
                }else
                {
                    likeLabel.text  = "\(String(likeInt))"
                }
            }
            else{
                likeLabel.text = "0"
            }
        }
        if let commentInt = (detailDictionary[COMMENTS] as? Int){
            if commentInt != 0{
                if commentInt == 1{
                    commentLabel.text = "\(String(commentInt))"
                }else{
                    commentLabel.text = "\(String(commentInt))"
                }
            } else{
                commentLabel.text = "0"
            }
        }
        let imageUrlDic = detailDictionary[ASSETS] as? NSDictionary
        if let assetUrl = imageUrlDic![ASSET_URL] as? String{
            let imageThumbUrl = imageUrlDic![THUMBNAIL_URL] as? String
            let asset_type = imageUrlDic![ASSET_TYPE] as? String
            if asset_type == "video"{
                if  imageThumbUrl != nil{
                    imgPlayVideo.isHidden = false
                    imageView.loadImageUsingCache(withUrl: imageThumbUrl!)
                }else{
                    print("do nothing")
                    imgPlayVideo.isHidden = true
                    imageView.image = UIImage(named: "noimage")
                }
            }
            else if asset_type == "image"{
                if  assetUrl != "" {
                    imgPlayVideo.isHidden = true
                    imageView.loadImageUsingCache(withUrl: assetUrl)
                }else{
                    print("do nothing")
                    imgPlayVideo.isHidden = true
                    imageView.image = UIImage(named: "novideo")
                }
                heightConstarints.constant = 372
            }
            else{
                imageView.isHidden = true
                imgPlayVideo.isHidden = true
                heightConstarints.constant = 0
                //lblConstraint.constant = 5
            }
        }
        
        if detailDictionary[CURRENT_USER_LIKE_POST] as? Int != nil {
            let likeIntUser = detailDictionary[CURRENT_USER_LIKE_POST] as? Int
            
            if likeIntUser == 0
            {
                
                
                likeBtn.setImage(UIImage(named: "Heart_line"), for: UIControlState.normal)
            }
            else
            {
                
                likeBtn.setImage(UIImage(named: "Heart"), for: UIControlState.normal)
            }
        }
        
        feedTypeLbl.text = detailDictionary[TITLE] as? String
        
        
    }
    
    func adjustUITextViewHeight(arg : UITextView)
    {
        arg.sizeToFit()
        arg.isScrollEnabled = false
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int
    {
        return arrComment.count
    }
    
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "MyCell", for: indexPath) as? UITableViewCell
        
        dict = arrComment.object(at: indexPath.row) as! NSDictionary
        
        if let Name = dict.value(forKey: "name")
        {
            print(Name)
            ByName = String(format:"%@",Name as! CVarArg)
            
        }
        if let Comment = dict.value(forKey:"comment")
        {
            print(Comment)
            CommentBy = Comment as! String
        }
        
        let formattedString = NSMutableAttributedString()
        formattedString
            .bold("\(ByName) : ")
            .normal("\(CommentBy)")
        
        let lblComment = cell?.contentView.viewWithTag(1) as! UILabel
        lblComment.attributedText = formattedString
        return cell!
        
        
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
    }
    
    
    
    @objc func imageTapped(_ sender:UITapGestureRecognizer){
        debugPrint("detailDictionary: \(detailDictionary!)")
        let socialPlatformName = detailDictionary[SOCIAL_PLATEFORM_NAME] as? String
        let assetDic = detailDictionary[ASSETS] as? NSDictionary
        if let assetUrl = assetDic![ASSET_URL] as? String{
            let asset_type = assetDic![ASSET_TYPE] as? String
            if asset_type == "video"{
                if socialPlatformName == "YouTube" {
                    let token = assetUrl.components(separatedBy: "embed/")
                    let vc=self.storyboard?.instantiateViewController(withIdentifier: YT_PLAYER_VIEW_CONTROLLER) as! YTPlayerViewController
                    vc.urlStr = token
                    self.navigationController?.pushViewController(vc, animated: true)
                }
                else {
                    if  assetUrl != "" {
                        let videoURL = URL(string: assetUrl)
                        debugPrint("videoURL: \( videoURL!)")
                        let player = AVPlayer(url: videoURL!)
                        let playerViewController = AVPlayerViewController()
                        playerViewController.player = player
                        self.present(playerViewController, animated: true) {
                            playerViewController.player!.play()
                        }
                    }else{}
                }
            }
            else if asset_type == "image"{
                if  assetUrl != "" {
                    let vc=self.storyboard?.instantiateViewController(withIdentifier: "ImageViewController") as! ImageViewController
                    vc.imageUrl = assetUrl
                    self.navigationController?.pushViewController(vc, animated: true)
                }else{
                    print("do nothing")
                }
            }
            else{
            }
        }
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func callForComments()
    {
        
        SVProgressHUD.show()
        if let userData = UserDefaults.standard.value(forKey: USERDATA) {
            let accessToken = (userData as AnyObject).value(forKey: ACCESS_TOKEN) as! String
            access_Token = accessToken
            print("Access:\(accessToken)")
        }
        
        let parameters: Parameters = [PAGE_NO:pageNo,FEED_ID:feed_id,FEEd_Unique_ID:feed_unique_Id]
        
        let headers: HTTPHeaders = ["Authorization": "\("Bearer ")\(access_Token!)"]
        Alamofire.request("\(BASE_URL)\(GET_COMMENTS)", method: .post , parameters: parameters , encoding: JSONEncoding.default, headers:headers ).responseObject { (response:DataResponse<User>)  in
            
            SVProgressHUD.dismiss()
            
            print("response: \(response)")
            
            if Connectivity.isConnectedToInternet() {
                if response.result.value != nil
                {
                    let json = response.result.value!
                    switch json.code{
                        
                    case "10"?:
                        if json.resultArray?.count == 0
                        {
                        }
                        else
                        {
                            self.arrComment = NSMutableArray(array: json.resultArray!)
                            print("self.ArrayComment: \(self.arrComment)")
                            self.tableView.reloadData()
                            
                        }
                        break
                    case "0"?:
                        self.addAlertView(title: json.status!, message: json.msg!, buttonTitle: CLICKOK)
                    case "1"?:
                        self.addAlertView(title: json.status!, message: json.msg!, buttonTitle: CLICKOK)
                    case "401"?:
                        Singleton.sharedInstance.refreshToken(arg: true, completion: { (success,jsonCode,jsonMsg)-> Void in
                            print("Second line of code executed")
                            if success { // this will be equal to whatever value is set in this method call
                                self.callForComments()
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
                else
                {
                    SVProgressHUD.dismiss()
                    //                    self.addAlertView(title: ALERT, message: SOMESERVERISSUE, buttonTitle: CLICKOK)
                }
            }
            else{
                self.addAlertView(title: ALERT, message: SOMESERVERISSUE, buttonTitle: CLICKOK)
            }
        }
        
        
    }
    
    
    
    @IBAction func backButton(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
    
    @IBOutlet weak var backButton: UIButton!
    func UTCToLocal(date:String) -> String {
        let dateFormatter = DateFormatter()
        
        dateFormatter.dateFormat = "yy-MM-dd HH:mm:ss"
        dateFormatter.timeZone = TimeZone(abbreviation: "UTC")
        let dt = dateFormatter.date(from: date)
        dateFormatter.timeZone = TimeZone.current
        dateFormatter.dateFormat = "M/dd/yyyy @ h:mm a"
        return dateFormatter.string(from: dt!)
    }
    
    func containsOnlyLetters(input: String) -> Bool {
        for chr in input {
            if (!(chr >= "a" && chr <= "z") && !(chr >= "A" && chr <= "Z") && !(chr == " " && chr == "\n") ) {
                return false
            }
        }
        return true
    }
    
    @IBAction func btnComment(_ sender: Any) {
        
        strComment = txtComment.text!
        
        //arrComment = []
        
        if let username = UserDefaults.standard.value(forKey: UERS_NAME_COMMENT) {
            
            let populatedDictionary = ["name": username, "comment": strComment]
            
            arrComment.insert(populatedDictionary, at: 0)
            
            print(arrComment)
        }
        
        if(!(txtComment.text?.isEmpty)!)
        {
            
            tableView.beginUpdates()
            tableView.insertRows(at: [IndexPath(row: 0 , section: 0)], with: .automatic)
            tableView.endUpdates()
            
            let lastSectionIndex = self.tableView!.numberOfSections - 1
            
            // Then grab the number of rows in the last section
            let lastRowIndex = self.tableView!.numberOfRows(inSection: lastSectionIndex) - 1
            
            // Now just construct the index path
            let pathToLastRow = NSIndexPath(row: 0, section: 0)
            
            self.tableView?.scrollToRow(at: pathToLastRow as IndexPath , at: UITableViewScrollPosition.none, animated: true)
            
            self.callForPostComment()
            txtComment.resignFirstResponder()
            
        }
        else
        {
            tableView.reloadData()
        }
        txtComment.text = ""
        txtComment.resignFirstResponder()
    }
    
    
    func callForPostComment()
    {
        SVProgressHUD.show()
        if let userData = UserDefaults.standard.value(forKey: USERDATA) {
            let accessToken = (userData as AnyObject).value(forKey: ACCESS_TOKEN) as! String
            access_Token = accessToken
            print("Access:\(accessToken)")
        }
        
        let parameters: Parameters = [FEED_ID:feed_id,FEEd_Unique_ID:feed_unique_Id,"comment":strComment]
        let headers: HTTPHeaders = ["Authorization": "\("Bearer ")\(access_Token!)"]
        Alamofire.request("\(BASE_URL)\(POST_COMMENTS)", method: .post , parameters: parameters , encoding: JSONEncoding.default, headers:headers ).responseObject { (response:DataResponse<User>)  in
            
            SVProgressHUD.dismiss()
            
            print("response: \(response)")
            
            if Connectivity.isConnectedToInternet() {
                if response.result.value != nil
                {
                    let json = response.result.value!
                    switch json.code{
                        
                    case "10"?:
                        if let commentCount = json.postComment
                        {
                            self.commentLabel.text = "\(String(commentCount))"
                        }
                        break
                    case "0"?:
                        self.addAlertView(title: json.status!, message: json.msg!, buttonTitle: CLICKOK)
                    case "1"?:
                        self.addAlertView(title: json.status!, message: json.msg!, buttonTitle: CLICKOK)
                    case "401"?:
                        Singleton.sharedInstance.refreshToken(arg: true, completion: { (success,jsonCode,jsonMsg)-> Void in
                            print("Second line of code executed")
                            if success { // this will be equal to whatever value is set in this method call
                                self.callForComments()
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
                else
                {
                    SVProgressHUD.dismiss()
                    //                    self.addAlertView(title: ALERT, message: SOMESERVERISSUE, buttonTitle: CLICKOK)
                }
            }
            else{
                self.addAlertView(title: ALERT, message: SOMESERVERISSUE, buttonTitle: CLICKOK)
            }
        }
        
    }
    
    
}

extension DetailFeedViewController : UIWebViewDelegate {
    
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


