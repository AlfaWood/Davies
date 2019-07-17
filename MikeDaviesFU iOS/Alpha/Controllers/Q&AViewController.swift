//
//  Q&AViewController.swift
//  Alpha
//
//  Created by Monika Tiwari on 13/04/18.
//  Copyright © 2018 Stpl. All rights reserved.
//

import UIKit
import SVProgressHUD
import Alamofire
import AlamofireObjectMapper
import ObjectMapper
import SDWebImage

class QAViewController: UIViewController, TabBarSwitcher,UITableViewDelegate, UITableViewDataSource{
    
    
    @IBOutlet weak var profileImgBtn: UIButton!
    
    var dictQandA = NSDictionary()
    var QandAArrayData = NSArray()
    var refreshControl = UIRefreshControl()
    
    @IBOutlet weak var tableView: UITableView!
    override func viewDidLoad() {
        super.viewDidLoad()
        initSwipe(direction: .right)
        initSwipe(direction: .left)
        QandAApiCall()
        self.tableView.estimatedRowHeight = 150.0
        
        self.refreshControl.addTarget(self, action: #selector(QAViewController.refresh(sender:)), for: UIControlEvents.valueChanged)
        self.tableView.addSubview(refreshControl)
    }
    
    @objc func refresh(sender:AnyObject){
        QandAApiCall()
        tableView.reloadData()
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
        
        if(QandAArrayData.count == 0)
        {
            QandAApiCall()
        }
        self.tableView.reloadData()
        self.tabBarController?.tabBar.isHidden = false
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
    
    // MARK:- CUSTOM FUNCTIONS
    // RESOURCE API
    func QandAApiCall(){
        SVProgressHUD.show()
        if let userData = UserDefaults.standard.value(forKey: USERDATA) {
            let accessToken = (userData as AnyObject).value(forKey: ACCESS_TOKEN) as! String
            access_Token = accessToken
            print("Access:\(accessToken)")
        }
        let headers: HTTPHeaders = ["Authorization": "\("Bearer ")\(access_Token!)"]
        
        let parameters: Parameters = ["app_id" : APPIDVALUE]
        Alamofire.request("\(BASE_URL)\(GET_QUESTION_ANSWER)", method: .post , parameters: parameters , encoding: JSONEncoding.default, headers:headers ).responseObject { (response:DataResponse<User>)  in
            SVProgressHUD.dismiss()
            
            if Connectivity.isConnectedToInternet() {
                if response.result.value != nil
                {
                    let json = response.result.value!
                    switch json.code{
                        
                    case "10"?:
                        if json.resultArray?.count == 0
                        {
                            self.addAlertView(title: json.status!, message: json.msg!, buttonTitle: CLICKOK)
                        }
                        else
                        {
                            self.QandAArrayData = NSMutableArray(array: json.resultArray!)
                            
                            if self.refreshControl.isRefreshing
                            {
                                self.refreshControl.endRefreshing()
                            }
                            
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
                            if success {
                                if jsonCode == "4"
                                {
                                    UserDefaults.standard.set(true, forKey: UPDATE_SUBSCRIPTION_USER)
                                    let alert = UIAlertController(title: ALERT, message: jsonMsg, preferredStyle: UIAlertControllerStyle.alert)
                                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: { (action: UIAlertAction!) in
                                        UserDefaults.standard.set(false, forKey: USERLOGGEDIN)
                                        let alert = UIAlertController(title: ALERT, message: json.msg!, preferredStyle: UIAlertControllerStyle.alert)
                                        alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: { (action: UIAlertAction!) in
                                            let vc = self.storyboard?.instantiateViewController(withIdentifier: SUBSCRIPTION_PLAN) as! MasterViewController
                                            self.navigationController?.setNavigationBarHidden(true, animated: false)
                                            self.navigationController?.pushViewController(vc, animated: true)
                                        }))
                                        self.present(alert, animated: true, completion: nil)
                                        
                                    }))
                                    self.present(alert, animated: true, completion: nil)
                                }
                                if jsonCode == "3"
                                {
                                    let alert = UIAlertController(title:json.status, message: jsonMsg, preferredStyle: UIAlertControllerStyle.alert)
                                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: { (action: UIAlertAction!) in
                                        UserDefaults.standard.set(false, forKey: USERLOGGEDIN)
                                        let vc=self.storyboard?.instantiateViewController(withIdentifier: LOGIN_VIEW_CONTROLLER) as! LoginViewController
                                        self.navigationController?.pushViewController(vc, animated: true)
                                        
                                    }))
                                    self.present(alert, animated: true, completion: nil)
                                }
                                else if jsonCode == "2"
                                {
                                    let alert = UIAlertController(title:json.status, message: jsonMsg, preferredStyle: UIAlertControllerStyle.alert)
                                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: { (action: UIAlertAction!) in
                                        UserDefaults.standard.set(false, forKey: USERLOGGEDIN)
                                        let vc=self.storyboard?.instantiateViewController(withIdentifier: LOGIN_VIEW_CONTROLLER) as! LoginViewController
                                        self.navigationController?.pushViewController(vc, animated: true)
                                        
                                    }))
                                    self.present(alert, animated: true, completion: nil)
                                }
                                else
                                {
                                    self.QandAApiCall()
                                }
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
                    //self.addAlertView(title: ALERT, message: SOMESERVERISSUE, buttonTitle: CLICKOK)
                }
            }
            else{
                self.addAlertView(title: ALERT, message: SOMESERVERISSUE, buttonTitle: CLICKOK)
            }
        }
    }
    
    func handleSwipes(sender:UISwipeGestureRecognizer) {
        if (sender.direction == .right) {
            tabBarController?.selectedIndex = 1
            navigationController?.popViewController(animated: true)
        }
        else  if (sender.direction == .left) {
            tabBarController?.selectedIndex = 3
            let vc=self.storyboard?.instantiateViewController(withIdentifier: MESSAGE_VIEW_CONTROLLER ) as! MessagesViewController
            show(vc, sender: nil)
        }
    }
    
    //MARK:- TABLE VIEW DELEGATES AND DATASOURCE
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return QandAArrayData.count
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell  = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath)
        
        let data = QandAArrayData.object(at: indexPath.row) as! NSDictionary
        let asset = data.value(forKey: "asset") as! NSDictionary
        
        let lblQuestn = cell.contentView.viewWithTag(1) as! UILabel
        let lblName = cell.contentView.viewWithTag(2) as! UILabel
        
        let lblAnswer = cell.contentView.viewWithTag(3) as! UILabel
        
        let readMoreBtnLbl = cell.contentView.viewWithTag(4) as! UILabel
        
        
        
        if((asset.value(forKey:"name") as? String) != nil)
        {
            lblName.text = (asset.value(forKey:"name") as! String)
        }
        
        
        lblAnswer.text = (asset.value(forKey:"answer") as! String)
        
        
        
        let strAnswer = "A: "
        let stringQuest = "Q: "
        
        let attrs = [NSAttributedStringKey.font : UIFont.boldSystemFont(ofSize: 14)]
        
        let appendStringQusert = "\(stringQuest) \(String(describing: asset.value(forKey:"question") as! String))"
        lblQuestn.text = appendStringQusert
        
        let appendAnswer = "\(strAnswer)\(String(describing: asset.value(forKey:"answer") as! String))"
        
        let string_to_color = strAnswer
        
        let range = (appendAnswer as NSString).range(of: string_to_color)
        
        let attribute = NSMutableAttributedString.init(string: appendAnswer)
        //   attribute.addAttribute(NSAttributedStringKey.foregroundColor, value: UIColor.bt_color(fromHex: "#3C77BD", alpha: 1.0) , range: range)
        
        attribute.addAttribute(NSAttributedStringKey.foregroundColor, value: UIColor(hexString: HEX_COLOUR).withAlphaComponent(1.0), range: range)
        attribute.addAttributes(attrs, range: range)
        
        
        lblAnswer.attributedText = attribute
        
        let labelTextSize = (lblAnswer.text as NSString?)?.boundingRect(
            with: CGSize(width: lblAnswer.frame.size.width, height: .greatestFiniteMagnitude),
            options: .usesLineFragmentOrigin,
            attributes: [.font: lblAnswer.font],
            context: nil).size
        if (labelTextSize?.height)! > lblAnswer.frame.size.height
        {
            readMoreBtnLbl.text = "Read More"
        }
        else
        {
            readMoreBtnLbl.text = ""
        }
        
        return cell
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let data1 = QandAArrayData.object(at:indexPath.row) as! NSDictionary
        
        dictQandA = data1.value(forKey:"asset") as! NSDictionary
        
        
        let vc = self.storyboard?.instantiateViewController(withIdentifier: "QandADetail") as! QandADetail
        
        vc.dictQustn = dictQandA
        self.navigationController?.pushViewController(vc, animated: true)
    }
    
    
    @IBAction func SubmitQuestionAction(_ sender: Any) {
        
        let vc=self.storyboard?.instantiateViewController(withIdentifier: SUBMITQUESTION_VIEW_CONTROLLER) as! SubmitQuestionViewController
        self.navigationController?.pushViewController(vc, animated: true)
        
    }
    @IBAction func btnProfile(_ sender: Any) {
        
        let vc=self.storyboard?.instantiateViewController(withIdentifier: USER_PROFILE_VIEW_CONTROLLER) as! UserProfileView
        vc.IsComingFrom = "other"
        self.navigationController?.pushViewController(vc, animated: true)
    }
}
