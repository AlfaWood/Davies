
//
//  HomeViewController.swift
//  Alpha
//
//  Created by Monika Tiwari on 06/04/18.
//  Copyright © 2018 Stpl. All rights reserved.
//

import UIKit
import MediaPlayer
import Alamofire
import AVFoundation
import AVKit
import SVProgressHUD
import Alamofire
import AlamofireObjectMapper
import ObjectMapper
import youtube_ios_player_helper
import SDWebImage

var feedArrayData:NSMutableArray = NSMutableArray()
var assets:NSArray!
var access_Token:String!
var isAlphaPost = true
var feedID = ""
var feed_unique_id = ""
var IsBtnClicked = ""


class FeedViewController: UIViewController, UITableViewDelegate, UITableViewDataSource,TabBarSwitcher, UIGestureRecognizerDelegate{
    
    var page : Int = 1
    var totalPageCount  = CGFloat()
    var NSMutableArrayUpdated = NSArray()
    
    @IBOutlet weak var profileImgBtn: UIButton!
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var topView: UIView!
    
    // VARIABLES:-
    var tableCell: TableViewCell!
    var feedArrayData : NSMutableArray = NSMutableArray()
    var refreshControl = UIRefreshControl()
    
    override func viewDidLoad(){
        super.viewDidLoad()
        tableView.delegate = self
        tableView.dataSource = self
        self.navigationController?.setNavigationBarHidden(false, animated: false)
        // set up the refresh control
        self.refreshControl.addTarget(self, action: #selector(FeedViewController.refresh(sender:)), for: UIControlEvents.valueChanged)
        self.tableView.addSubview(refreshControl)
        initSwipe(direction: .left)
        tableView.estimatedRowHeight = 68.0
        tableView.rowHeight = UITableViewAutomaticDimension
        FeedApiCall()
        self.tableView.reloadData()
        self.tabBarController?.tabBar.isHidden = true
        
        if navigationController!.responds(to:#selector(getter: UINavigationController.interactivePopGestureRecognizer)) {
            navigationController!.view.removeGestureRecognizer(navigationController!.interactivePopGestureRecognizer!)
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(true)
        
        /*
         let navView = UIView()
         navView.frame =  CGRect(x:-350, y:-10, width:950, height:65)
         let image = UIImageView()
         image.image = UIImage(named: "DPE-Inline")
         image.frame = CGRect(x:-350, y:-10, width:950, height:65)
         image.contentMode = UIViewContentMode.scaleAspectFit
         navView.addSubview(image)
         self.navigationItem.titleView = navView
         navView.sizeToFit()
         */
        
        updateNavigationBarUI()
        
        let str = UserDefaults.standard.value(forKey: FCM_TOKEN)
        
        print("TOKEN : \(str!)")
        
        if let profilePic = UserDefaults.standard.value(forKey: PROFILE_PIC) as? String{
            print("Profile Pic URL : \(profilePic)")
            self.profileImgBtn.sd_setBackgroundImage(with: URL(string: profilePic), for: UIControlState.normal, placeholderImage: UIImage(named: "Profile") ,completed: nil)
            let widthConstraint = profileImgBtn.widthAnchor.constraint(equalToConstant: 35)
            let heightConstraint = profileImgBtn.heightAnchor.constraint(equalToConstant: 35)
            heightConstraint.isActive = true
            widthConstraint.isActive = true
            self.profileImgBtn.layer.cornerRadius =   self.profileImgBtn.frame.height / 2
            self.profileImgBtn.clipsToBounds = true
            
        }
        
        Singleton.sharedInstance.requestPOSTURL(success: { (message) in
            // success code
            print("response json -->>\(message)")
            if let tabItems = self.tabBarController?.tabBar.items as NSArray?
            {
                // In this case we want to modify the badge number of the third tab:
                let tabItem = tabItems[3] as! UITabBarItem
                if message == "0" {
                    tabItem.badgeValue = nil
                }else{
                    tabItem.badgeValue = message
                }
            }
        }, failure: { (error) in
            //error code
            print(error)
        })
        
        print("app key -- \((UserDefaults.standard.string(forKey: APP_LAUNCH))!)")
        
        if UserDefaults.standard.value(forKey: APP_LAUNCH) != nil &&
            UserDefaults.standard.value(forKey: APP_LAUNCH) as! Bool == true
        {
            self.tabBarController?.tabBar.isHidden = true
            self.navigationController?.setNavigationBarHidden(true, animated: false)
        }
        else
        {
            SVProgressHUD.show()
            self.tabBarController?.tabBar.isHidden = false
            self.navigationController?.setNavigationBarHidden(false, animated: false)
        }
        
        self.FeedApiCall()
        
        self.navigationController?.interactivePopGestureRecognizer?.isEnabled = false
        
        for recognizer in view.gestureRecognizers! {
            view.removeGestureRecognizer(recognizer)
        }
        let statusBar: UIView = UIApplication.shared.value(forKey: "statusBar") as! UIView
        statusBar.isHidden = true
        page = 1
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
        
        self.profileImgBtn.imageEdgeInsets = UIEdgeInsets(top: 10, left: 0, bottom: 10, right: 0)
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    @objc func refresh(sender:AnyObject){
        page = 1
        FeedApiCall()
        tableView.reloadData()
    }
    
    //MARK:- TABLE VIEW DELEGATES AND DATASOURCE
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return feedArrayData.count
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let data = feedArrayData.object(at: indexPath.row) as! NSDictionary
        var cell = tableView.dequeueReusableCell(withIdentifier: "feed", for: indexPath) as! TableViewCell
        if  let assetDic = data[ASSETS] as? NSDictionary {
            
            let asset_type = assetDic[ASSET_TYPE] as? String
            if asset_type == "text"{
                cell  = tableView.dequeueReusableCell(withIdentifier: "feedtext", for: indexPath) as! TableViewCell
            }
            else{
                cell  = tableView.dequeueReusableCell(withIdentifier: "feed", for: indexPath) as! TableViewCell
            }
            
        }
        
        
        cell.heartBtn.addTarget(self, action: #selector(self.pressButtonHeart(_:)), for: .touchUpInside) //<- use `#selector(...)`
        cell.heartBtn.tag = indexPath.row
        
        cell.commentBtn.addTarget(self, action: #selector(self.pressButtonComment(_:)), for: .touchUpInside) //<- use `#selector(...)`
        cell.commentBtn.tag = indexPath.row
        
        
        if  data[DATE_TIME] as? String != nil {
            let dateTime = data[DATE_TIME] as! String
            cell.cellDateTimeLbl.text =  dateTime
        }
        if data[FEED_TYPE] as? String != nil {
            cell.cellFeedTypeLbl.text = (data[FEED_TYPE] as! String)
        }
        
        cell.cellFeedTypeLbl.text = (data[TITLE] as! String)
        if data[STORYDATA] as? String != nil{
            cell.cellDescriptionLbl.text = (data[STORYDATA] as! String)
            let labelTextSize = (cell.cellDescriptionLbl.text as NSString?)?.boundingRect(
                with: CGSize(width: cell.cellDescriptionLbl.frame.size.width, height: .greatestFiniteMagnitude),
                options: .usesLineFragmentOrigin,
                attributes: [.font: cell.cellDescriptionLbl.font],
                context: nil).size
            if (labelTextSize?.height)! > cell.cellDescriptionLbl.frame.size.height-10
            {
                cell.readMoreLBL.text = "Read More"
            }
            else
            {
                cell.readMoreLBL.text = ""
            }
            
            cell.cellDescriptionLbl.text = String(format : "%@\n",(data[STORYDATA] as! String))
            
        }
        if data[ALPHA_COMMENTED] as? Int != nil {
            let alphaCommented = data[ALPHA_COMMENTED] as? Int
            if alphaCommented == 0
            {
                cell.alphaCommentLbl.text = ""
            }
            else
            {
                cell.alphaCommentLbl.text = data[ALPHA_NAME] as? String
            }
        }
        
        let firstName = data[FIRST_COMMENT_NAME] as! String
        let firstMsg = data[FIRST_COMMENT] as! String
        
        let secondName = data[SECOND_COMMENT_NAME] as! String
        let secondMsg = data[SECOND_COMMENT] as! String
        
        
        if ((data[FIRST_COMMENT] as? String != "") && (data[SECOND_COMMENT] as? String == "")) {
            let formattedString = NSMutableAttributedString()
            formattedString
                .bold("\(firstName) : ")
                .normal("\(firstMsg)\n")
            cell.firstCommentLbl.attributedText = formattedString
            print("comment for one \(formattedString)")
        }
        else if (data[SECOND_COMMENT] as? String != "") {
            
            let formattedString = NSMutableAttributedString()
            formattedString
                .bold("\(firstName) : ")
                .normal("\(firstMsg)")
            
            let formattedStringtwo = NSMutableAttributedString()
            formattedStringtwo
                .bold("\(secondName) : ")
                .normal("\(secondMsg)\n")
            
            cell.firstCommentLbl.attributedText = formattedString
            cell.commentsLbl.attributedText = formattedStringtwo
        }
        else
        {
            cell.commentsLbl.text = ""
            cell.firstCommentLbl.text = ""
        }
        
        if data[CURRENT_USER_LIKE_POST] as? Int != nil {
            let likeIntUser = data[CURRENT_USER_LIKE_POST] as? Int
            if likeIntUser == 0
            {
                cell.likeImageView.image = UIImage(named:"Heart_line")
            }
            else
            {
                cell.likeImageView.image = UIImage(named:"Heart")
            }
        }
        if data[LIKE] as? Int != nil {
            if let likeInt = data[LIKE] as? Int {
                if likeInt != 0{
                    cell.cellLikeLbl.text  = "\(String(likeInt))"
                }
                else{
                    cell.cellLikeLbl.text  = "\(String(likeInt))"
                }
            }
        }
        
        if  data[COMMENTS] as? Int != nil{
            if let commentInt = data[COMMENTS] as? Int {
                if commentInt != 0{
                    cell.cellCommentsLbl.text = "\(String(commentInt))"
                } else{
                    cell.cellCommentsLbl.text  = "\(String(commentInt))"
                }
            }
        }
        
        //Code commented for testing
        if  let assetDic = data[ASSETS] as? NSDictionary {
            let assetUrl = assetDic[ASSET_URL] as? String
            let ThumbUrl = assetDic[THUMBNAIL_URL] as? String
            let asset_type = assetDic[ASSET_TYPE] as? String
            print("Asset type : \(asset_type!)")
            if asset_type == "text"
            {
                
                
            }
            else if asset_type == "image"{
                print("Assert Url :\(assetUrl!)")
                if  assetUrl != nil && assetUrl != ""
                    
                {
                    
                    cell.cellImageVideoView.isHidden = true
                    cell.cellImageView.sd_setImage(with: URL(string: assetUrl!), placeholderImage: UIImage(named: ""))
                    
                }
            }
            else if asset_type == "video"  {
                if  ThumbUrl != nil && ThumbUrl != ""
                {
                    
                    cell.cellImageVideoView.isHidden = false
                    cell.cellImageView.sd_setImage(with: URL(string: ThumbUrl!), placeholderImage: UIImage(named: ""))
                    //
                }
            }
        }
        return cell
    }
    
    //The target function
    @objc func pressButtonHeart(_ sender: UIButton){ //<- needs `@objc`
        print("\(sender.tag)")
        
        let indexPath = NSIndexPath(row: sender.tag, section: 0)
        let cell = tableView.cellForRow(at: indexPath as IndexPath) as! TableViewCell?
        
        var likeCount:Int? = Int((cell?.cellLikeLbl.text)!)
        
        self.beginAnimation(imgView: (cell?.likeImageView)!)
        
        var data = feedArrayData.object(at:sender.tag) as! Dictionary<String, Any>
        
        if data[CURRENT_USER_LIKE_POST] as? Int != nil {
            let likeIntUser = data[CURRENT_USER_LIKE_POST] as? Int
            
            if likeIntUser == 0
            {
                //blank heart
                IsBtnClicked = "1"
                cell?.likeImageView.image = UIImage(named:"Heart")
                likeCount = likeCount! + 1
                data[CURRENT_USER_LIKE_POST] = 1
                print("current user like count \(data[CURRENT_USER_LIKE_POST]!)")
            }
            else
            {
                //filled heart
                IsBtnClicked = "0"
                cell?.likeImageView.image = UIImage(named:"Heart_line")
                if likeCount != 0
                {
                    likeCount = likeCount! - 1
                }
                data[CURRENT_USER_LIKE_POST] = 0
                print("current user like count \(data[CURRENT_USER_LIKE_POST]!)")
            }
            
        }
        data[LIKE] = likeCount
        feedID = String(format: "%@", data[FEED_ID] as! CVarArg)
        feed_unique_id = String(format: "%@", data[FEEd_Unique_ID] as! CVarArg)
        cell?.cellLikeLbl.text =  "\(String(describing: likeCount!))"
        print("\(feedID)\n\(feed_unique_id)")
        
        feedArrayData.replaceObject(at: indexPath.row, with: data)
        print("Feed data array--> \(feedArrayData)")
        
        self.callforLike()
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
            print("response: \(response)")
            if Connectivity.isConnectedToInternet() {
                if response.result.value != nil
                {
                    self.view.alpha = 1.0
                    let json = response.result.value!
                    switch json.code{
                    case "10"?:
                        print(json.msg as Any)
                        //                    self.FeedApiCallLoadMore(page: page)
                        //                    self.FeedApiCall()
                        DispatchQueue.main.async
                            {
                                //  self.tableView.reloadData()
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
                                self.FeedApiCall()
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
    
    //The target function
    @objc func pressButtonComment(_ sender: UIButton){ //<- needs `@objc`
        let data = feedArrayData.object(at: sender.tag) as! NSDictionary
        let vc = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: DETAIL_FEED_VIEW_CONTROLLER) as! DetailFeedViewController
        vc.detailDictionary = data
        navigationController?.pushViewController(vc,animated: true)
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let data = feedArrayData.object(at: indexPath.row) as! NSDictionary
        let vc = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: DETAIL_FEED_VIEW_CONTROLLER) as! DetailFeedViewController
        vc.detailDictionary = data
        navigationController?.pushViewController(vc,animated: true)
    }
    
    //MARK:- CUSTOM FUNCTIONS
    // FEED API
    
    func FeedApiCall(){
        
        let defaults = UserDefaults.standard
        let accessToken = defaults.string(forKey: "accessToken")
        
        if accessToken != nil {
            access_Token = accessToken
        }
        else{
            access_Token = ""
        }
        let parameters: Parameters = ["page":"1","app_id" : APPIDVALUE]
        
        let headers: HTTPHeaders = ["Authorization": "\("Bearer ")\(access_Token!)"]
        Alamofire.request("\(BASE_URL)\(FEED_API)", method: .post, parameters: parameters, encoding: JSONEncoding.default, headers:headers ).responseJSON { (response) in
            debugPrint("response: \(response)")
            if Connectivity.isConnectedToInternet() {
                if response.result.value != nil
                {
                    SVProgressHUD.dismiss()
                    let json = response.result.value! as! NSDictionary
                    print(json)
                    switch json[CODE] as! String
                    {
                    case "10":
                        SVProgressHUD.dismiss()
                        self.feedArrayData = NSMutableArray(array: json.value(forKey: RESULT) as! NSArray)
                        print("self.feedArrayData: \(self.feedArrayData)")
                        if (self.feedArrayData.count != 0)
                        {
                            self.totalPageCount = json.value(forKey: "total_data") as! CGFloat
                        }
                        
                        self.topView.isHidden = true
                        self.tabBarController?.tabBar.isHidden = false
                        self.navigationController?.setNavigationBarHidden(false, animated: false)
                        if self.refreshControl.isRefreshing
                        {
                            self.refreshControl.endRefreshing()
                        }
                        let defaults = UserDefaults.standard
                        defaults.set(false, forKey: APP_LAUNCH)
                        self.tableView.reloadData()
                        break
                    case "0":
                        self.addAlertView(title: json[STATUS] as! String, message: json[MESSAGE] as! String, buttonTitle: CLICKOK)
                    case "1":
                        self.addAlertView(title: json[STATUS] as! String, message: json[MESSAGE] as! String, buttonTitle: CLICKOK)
                    case "401":
                        Singleton.sharedInstance.refreshToken(arg: true, completion: { (success,jsonCode,jsonMsg) -> Void in
                            print("Message \(jsonCode)")
                            if success {
                                self.navigationController?.setNavigationBarHidden(false, animated: false)
                                
                                self.tabBarController?.tabBar.isHidden = true
                                
                                if jsonCode == "4"
                                {
                                    let alert = UIAlertController(title: ALERT, message: jsonMsg, preferredStyle: UIAlertControllerStyle.alert)
                                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: { (action: UIAlertAction!) in
                                        UserDefaults.standard.set(false, forKey: USERLOGGEDIN)
                                        let vc=self.storyboard?.instantiateViewController(withIdentifier: SUBSCRIPTION_PLAN) as! MasterViewController
                                        self.navigationController?.pushViewController(vc, animated: true)
                                        //   let addCardViewController = STPAddCardViewController()
                                        //    addCardViewController.delegate = self
                                        // Present add card view controller
                                        //  self.navigationController?.pushViewController(addCardViewController, animated: true)
                                        self.tabBarController?.tabBar.isHidden = true
                                        
                                    }))
                                    self.present(alert, animated: true, completion: nil)
                                }
                                if jsonCode == "3"
                                {
                                    
                                    let alert = UIAlertController(title:json[STATUS] as? String, message:jsonMsg, preferredStyle: UIAlertControllerStyle.alert)
                                    
                                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: { (action: UIAlertAction!) in
                                        UserDefaults.standard.set(false, forKey: USERLOGGEDIN)
                                        
                                        
                                        let vc=self.storyboard?.instantiateViewController(withIdentifier: LOGIN_VIEW_CONTROLLER) as! LoginViewController
                                        self.navigationController?.pushViewController(vc, animated: true)
                                        
                                    }))
                                    self.present(alert, animated: true, completion: nil)
                                }
                                else
                                {
                                    self.FeedApiCall()
                                }
                            }
                            else if jsonCode == "2"
                            {
                                let alert = UIAlertController(title:json[STATUS] as? String, message:jsonMsg, preferredStyle: UIAlertControllerStyle.alert)
                                
                                alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: { (action: UIAlertAction!) in
                                    UserDefaults.standard.set(false, forKey: USERLOGGEDIN)
                                    
                                    
                                    let vc=self.storyboard?.instantiateViewController(withIdentifier: LOGIN_VIEW_CONTROLLER) as! LoginViewController
                                    self.navigationController?.pushViewController(vc, animated: true)
                                    
                                }))
                                self.present(alert, animated: true, completion: nil)
                            }
                            else {
                            }
                        }, failure: {(alert,message) -> Void in
                            self.addAlertView(title: alert, message: message, buttonTitle: CLICKOK)
                        }
                        )
                        break
                    default:
                        //     self.addAlertView(title: ALERT, message: SOMESERVERISSUE, buttonTitle: CLICKOK)
                        SVProgressHUD.dismiss()
                        break
                    }
                }
                else
                {
                    //                    self.addAlertView(title: ALERT, message: SOMESERVERISSUE, buttonTitle: CLICKOK)
                    SVProgressHUD.dismiss()
                    
                }
            }
            else{
                self.addAlertView(title: ALERT, message: SOMESERVERISSUE, buttonTitle: CLICKOK)
                SVProgressHUD.dismiss()
                
            }
        }
    }
    
    func FeedApiCallLoadMore(page:Int){
        SVProgressHUD.show()
        let defaults = UserDefaults.standard
        if let accessToken = defaults.string(forKey: "accessToken") {
            access_Token = accessToken
        }
        
        let parameters: Parameters = ["page":String(page),"app_id" : APPIDVALUE]
        // self.tabBarController?.tabBar.isHidden = true
        // let parameters: Parameters = ["page" :String(page) ]
        
        let headers: HTTPHeaders = ["Authorization": "\("Bearer ")\(access_Token!)"]
        Alamofire.request("\(BASE_URL)\(FEED_API)", method: .post, parameters: parameters, encoding: JSONEncoding.default, headers:headers ).responseJSON { (response) in
            debugPrint("response: \(response)")
            if Connectivity.isConnectedToInternet() {
                SVProgressHUD.dismiss()
                if response.result.value != nil
                {
                    SVProgressHUD.dismiss()
                    let json = response.result.value! as! NSDictionary
                    print(json)
                    switch json[CODE] as! String
                    {
                    case "10":
                        SVProgressHUD.dismiss()
                        self.NSMutableArrayUpdated = NSMutableArray(array: json.value(forKey: RESULT) as! NSArray)
                        
                        if self.refreshControl.isRefreshing
                        {
                            self.refreshControl.endRefreshing()
                        }
                        self.feedArrayData.addObjects(from: (self.NSMutableArrayUpdated as! NSMutableArray) as! [Any])
                        self.tableView.reloadData()
                        break
                    case "0":
                        self.addAlertView(title: json[STATUS] as! String, message: json[MESSAGE] as! String, buttonTitle: CLICKOK)
                    case "1":
                        self.addAlertView(title: json[STATUS] as! String, message: json[MESSAGE] as! String, buttonTitle: CLICKOK)
                    case "401":
                        Singleton.sharedInstance.refreshToken(arg: true, completion: { (success,jsonCode,jsonMsg) -> Void in
                            print("Message \(jsonCode)")
                            if success {
                                if jsonCode == "4"
                                {
                                    let alert = UIAlertController(title: ALERT, message: jsonMsg, preferredStyle: UIAlertControllerStyle.alert)
                                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: { (action: UIAlertAction!) in
                                        UserDefaults.standard.set(false, forKey: USERLOGGEDIN)
                                        let vc=self.storyboard?.instantiateViewController(withIdentifier: SUBSCRIPTION_PLAN) as! MasterViewController
                                        self.navigationController?.pushViewController(vc, animated: true)
                                        //      let addCardViewController = STPAddCardViewController()
                                        //     addCardViewController.delegate = self
                                        // Present add card view controller
                                        //    self.navigationController?.pushViewController(addCardViewController, animated: true)
                                        self.tabBarController?.tabBar.isHidden = true
                                        
                                    }))
                                    self.present(alert, animated: true, completion: nil)
                                }
                                if jsonCode == "3"
                                {
                                    let alert = UIAlertController(title:json[STATUS] as? String, message: jsonMsg, preferredStyle: UIAlertControllerStyle.alert)
                                    
                                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: { (action: UIAlertAction!) in
                                        UserDefaults.standard.set(false, forKey: USERLOGGEDIN)
                                        let vc=self.storyboard?.instantiateViewController(withIdentifier: LOGIN_VIEW_CONTROLLER) as! LoginViewController
                                        self.navigationController?.pushViewController(vc, animated: true)
                                        
                                    }))
                                    self.present(alert, animated: true, completion: nil)
                                }
                                else if jsonCode == "2"
                                {
                                    let alert = UIAlertController(title:json[STATUS] as? String, message: jsonMsg, preferredStyle: UIAlertControllerStyle.alert)
                                    
                                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: { (action: UIAlertAction!) in
                                        UserDefaults.standard.set(false, forKey: USERLOGGEDIN)
                                        let vc=self.storyboard?.instantiateViewController(withIdentifier: LOGIN_VIEW_CONTROLLER) as! LoginViewController
                                        self.navigationController?.pushViewController(vc, animated: true)
                                        
                                    }))
                                    self.present(alert, animated: true, completion: nil)
                                }
                                else
                                {
                                    self.FeedApiCall()
                                }
                            } else {
                            }
                        }, failure: {(alert,message) -> Void in
                            self.addAlertView(title: alert, message: message, buttonTitle: CLICKOK)
                        }
                        )
                        break
                    default:
                        break
                    }
                }
                else
                {
                    //                    self.addAlertView(title: ALERT, message: SOMESERVERISSUE, buttonTitle: CLICKOK)
                    SVProgressHUD.dismiss()
                    
                }
            }
            else{
                SVProgressHUD.dismiss()
                self.addAlertView(title: ALERT, message: SOMESERVERISSUE, buttonTitle: CLICKOK)
            }
        }
    }
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        if indexPath.row == self.feedArrayData.count-1 {
            print ("load more api will display cell")
            let number:CGFloat = CGFloat(totalPageCount/10)
            let roundedFloat = CGFloat(ceil(Double(number)))
            print("value is \(roundedFloat)")
            if(page <= Int(roundedFloat))
            {
                page = page + 1
                FeedApiCallLoadMore(page: page)
            }
            else
            {
                print("No more data")
            }
        }
    }
    
    // Convert server UTC time to local time
    func UTCToLocal(date:String) -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd hh:mm:ss"
        dateFormatter.timeZone = TimeZone(abbreviation: "UTC")
        
        let dt = dateFormatter.date(from: date)
        dateFormatter.timeZone = TimeZone.current
        dateFormatter.dateFormat = "MM/dd/yyyy @ h:mma"
        return dateFormatter.string(from: dt!)
        
    }
    func handleSwipes(sender:UISwipeGestureRecognizer) {
        if (sender.direction == .left) {
            tabBarController?.selectedIndex = 1
            let vc = UIStoryboard(name: "Main", bundle: nil).instantiateViewController(withIdentifier: RESOURCE_VIEW_CONTROLLER) as! ResourceViewController
            show(vc, sender: nil)
        }
    }
    @IBAction func logOutAction(_ sender: Any) {
        let vc=self.storyboard?.instantiateViewController(withIdentifier: LOGIN_VIEW_CONTROLLER) as! LoginViewController
        show(vc, sender: self)
    }
    @IBAction func btnProfile(_ sender: Any) {
        
        let vc=self.storyboard?.instantiateViewController(withIdentifier: USER_PROFILE_VIEW_CONTROLLER) as! UserProfileView
        vc.IsComingFrom = "Feed"
        self.navigationController?.pushViewController(vc, animated: true)
    }
    func beginAnimation (imgView : UIImageView) {
        UIView.animate(withDuration: 0.6, delay:0, options: [.repeat, .autoreverse], animations: {
            UIView.setAnimationRepeatCount(1)
            imgView.transform = CGAffineTransform(scaleX: 1.5, y: 1.5)
        }, completion: {completion in
            imgView.transform = CGAffineTransform(scaleX: 1, y: 1)
            
        })
        
    }
}

