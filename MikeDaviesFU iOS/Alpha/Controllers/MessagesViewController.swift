//
//  MessagesViewController.swift
//  Alpha
//
//  Created by Razan Nasir on 13/04/18.
//  Copyright © 2018 Stpl. All rights reserved.
//

import UIKit
import SVProgressHUD
import Alamofire
import SDWebImage

class MessagesViewController: UIViewController,TabBarSwitcher, UITableViewDelegate, UITableViewDataSource,UISearchBarDelegate {
    //OUTLETS
    
    
    @IBOutlet weak var profileImgBtn: UIButton!
    
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var searchBar: UISearchBar!
    var arrNames:NSArray = []
    var isEndSearch = false
    var isSelectedSearch = false
    var isPageRefreshing = false
    var page : Int = 1
    var refreshControl = UIRefreshControl()
    var totalPageCount  = CGFloat()
    // VARIABLES
    var messageArrayData = NSMutableArray()
    var NSMutableArrayUpdated = NSArray()
    
    var searchedArray:[String] = []
    var searchActive : Bool = false
    //var filteredTableData:[String] = []
    var filteredTableData = NSArray()
    var assetDictionary = NSMutableDictionary()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //NAVIGATION BAR
        navigationItem.title = APP_NAME
        
        initSwipe(direction: .right)
        
        self.refreshControl.addTarget(self, action: #selector(MessagesViewController.refresh(sender:)), for: UIControlEvents.valueChanged)
        self.tableView.addSubview(refreshControl)
        
        
        //ProfilePIc
        
        if let profilePic = UserDefaults.standard.value(forKey: PROFILE_PIC) as? String{
            print("Profile Pic URL : \(profilePic)")
            self.profileImgBtn.sd_setBackgroundImage(with: URL(string: profilePic), for: UIControlState.normal, completed: nil)
            
            let widthConstraint = profileImgBtn.widthAnchor.constraint(equalToConstant: 35)
            let heightConstraint = profileImgBtn.heightAnchor.constraint(equalToConstant: 35)
            heightConstraint.isActive = true
            widthConstraint.isActive = true
            
            self.profileImgBtn.layer.cornerRadius =   self.profileImgBtn.frame.height / 2
            self.profileImgBtn.clipsToBounds = true
            
        }
        
        
        //SEARCHBAR
        searchBar.delegate = self
        searchBar.setImage(UIImage(named: "search"), for: .search, state: .normal)
        let searchTextField: UITextField? = searchBar.value(forKey: "searchField") as? UITextField
        if searchTextField!.responds(to: #selector(getter: UITextField.attributedPlaceholder)) {
            // let attributeDict = [NSAttributedStringKey.foregroundColor: UIColor.bt_color(fromHex:"3C77BD", alpha: 1.0)]
            let attributeDict = [NSAttributedStringKey.foregroundColor:  UIColor(hexString: HEX_COLOUR).withAlphaComponent(1.0)]
            
            searchTextField!.attributedPlaceholder = NSAttributedString(string: "Type Here to Search", attributes: (attributeDict as Any as! [NSAttributedStringKey : Any]) )
        }
        UITextField.appearance(whenContainedInInstancesOf: [UISearchBar.self]).font = UIFont(name: "HelveticaNeue-Regular", size: 14.0)
        
        let singleTapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(self.singleTap(sender:)))
        singleTapGestureRecognizer.numberOfTapsRequired = 1
        singleTapGestureRecognizer.isEnabled = true
        singleTapGestureRecognizer.cancelsTouchesInView = false
        self.view.addGestureRecognizer(singleTapGestureRecognizer)
        
    }
    
    @objc func refresh(sender:AnyObject){
        page = 1
        MessageApiCall()
        searchBar.text = ""
        searchActive = false
        tableView.reloadData()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        /*
         let navView = UIView()
         navView.frame =  CGRect(x:-350, y:-10, width:950, height:65)
         let image = UIImageView()
         image.image = UIImage(named: "DPE-Inline")
         image.frame = CGRect(x:-350, y:-10, width:950, height:65)
         
         // Add both the label and image view to the navView
         navView.addSubview(image)
         
         // Set the navigation bar's navigation item's titleView to the navView
         self.navigationItem.titleView = navView
         
         // Set the navView's frame to fit within the titleView
         navView.sizeToFit()
         
         image.contentMode = UIViewContentMode.scaleAspectFit
         */
        
        updateNavigationBarUI()
        
        self.tabBarController?.tabBar.isHidden = false
        self.navigationController?.setNavigationBarHidden(false, animated: false)
        
        if let profilePic = UserDefaults.standard.value(forKey: PROFILE_PIC) as? String{
            print("Profile Pic URL : \(profilePic)")
            self.profileImgBtn.sd_setBackgroundImage(with: URL(string: profilePic), for: UIControlState.normal, placeholderImage: UIImage(named: "Profile") ,completed: nil)
            
            let widthConstraint = profileImgBtn.widthAnchor.constraint(equalToConstant: 35)
            let heightConstraint = profileImgBtn.heightAnchor.constraint(equalToConstant: 35)
            heightConstraint.isActive = true
            widthConstraint.isActive = true
            
            self.profileImgBtn.layer.cornerRadius =   self.profileImgBtn.frame.height / 2
            self.profileImgBtn.clipsToBounds = true
            
            if UserDefaults.standard.bool(forKey:COMNIG_FROM_NOTIFICATION)
            {
                let defaults = UserDefaults.standard
                defaults.set(false, forKey: COMNIG_FROM_NOTIFICATION)
                let vc=self.storyboard?.instantiateViewController(withIdentifier: "MessageDetailViewController") as! MessageDetailViewController
                defaults.set(true, forKey: COMING_FROM_NOTIFICATION)
                let notifDict = defaults.value(forKey: NOTIF_DICT) as! NSDictionary
                print("Received Notif dict : \(notifDict)")
                vc.messageDetailDictionary = notifDict
                self.navigationController?.pushViewController(vc, animated: true)
            }
            
        }
        
        
        searchBar.text = ""
        isSelectedSearch = false
        
        searchActive = false
        
        
        self.tabBarController?.tabBar.isHidden = false
        self.navigationController?.setNavigationBarHidden(false, animated: false)
        
        searchBar.text = ""
        
        Singleton.sharedInstance.requestPOSTURL(success: { (message) in
            // success code
            if let tabItems = self.tabBarController?.tabBar.items as NSArray?
            {
                // In this case we want to modify the badge number of the third tab:
                let tabItem = tabItems[3] as! UITabBarItem
                if message == "0" {
                    tabItem.badgeValue = nil
                }else{
                    tabItem.badgeValue = message
                }
                self.tableView.reloadData()
            }
        }, failure: { (error) in
            //error code
            print(error)
        })
        
        MessageApiCall()
        tableView.reloadData()
        self.tabBarController?.tabBar.isHidden = false
        self.navigationController?.setNavigationBarHidden(false, animated: false)
        
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
    }
    
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    @objc func singleTap(sender: UITapGestureRecognizer) {
        self.searchBar.resignFirstResponder()
    }
    //MARK:- SEARCHBAR DELEGATES
    
    func searchBarTextDidBeginEditing(_ searchBar: UISearchBar)
    {
        
        isEndSearch = false
        
    }
    func searchBarTextDidEndEditing(_ searchBar: UISearchBar)
    {
        searchActive = false
        isEndSearch = true
        self.tableView.isHidden = false
        
    }
    
    
    func searchBarCancelButtonClicked(_ searchBar: UISearchBar) {
        self.tableView.isHidden = false
        searchActive = false
        tableView.reloadData()
    }
    
    func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
        self.searchBar.endEditing(true)
        
    }
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        
        if(isEndSearch == true || searchText.count == 0)
        {
            searchActive = false
            isSelectedSearch = false
        }
        else
        {
            isSelectedSearch = true
            searchActive = true
        }
        
        let arrNames:NSMutableArray = []
        
        for data in messageArrayData
        {
            var dict = data as![String:Any]
            
            arrNames.add(dict["asset"]!)
            
        }
        
        let namePredicate = NSPredicate(format: "(title contains[c] %@) OR (from contains[c] %@) ",searchText,searchText);
        let filteredArray = arrNames.filter { namePredicate.evaluate(with: $0) } as NSArray;
        
        filteredTableData = filteredArray
        self.tableView.reloadData()
    }
    //MARK:- TABLEVIEW DELEGATES AND DATASOURCE
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if (searchActive)
        {
            return filteredTableData.count
        }
        else
        {
            return messageArrayData.count
        }
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "messageCellId", for: indexPath)
        
        var asset = NSDictionary()
        
        if(searchActive)
        {
            asset = filteredTableData[indexPath.row]as! NSDictionary
            (cell.contentView.viewWithTag(10) as! UILabel).text = filteredTableData[indexPath.row] as? String
        }
        else
        {
            let data = messageArrayData.object(at: indexPath.row) as! NSDictionary
            asset = data.value(forKey: "asset") as! NSDictionary
        }
        let dateTime = asset.value(forKey: CREATED_AT) as? String
        (cell.contentView.viewWithTag(10) as! UILabel).text =
            asset[FROM] as? String;
        (cell.contentView.viewWithTag(20) as! UILabel).text =  dateTime
        (cell.contentView.viewWithTag(30) as! UILabel).text = asset.value(forKey: TITLE) as? String
        (cell.contentView.viewWithTag(40) as! UILabel).text = asset.value(forKey: DESCRIPTION_TEXT) as? String
        
        if ((asset.value(forKey: MESSAGE_READ) as? Bool) == true) {
            (cell.contentView.viewWithTag(50) as! UIImageView).isHidden = true
        }
        else
        {
            (cell.contentView.viewWithTag(50) as! UIImageView).isHidden = false
        }
        
        return cell
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        if(isSelectedSearch)
        {
            var asset1 =   NSDictionary()
            isSelectedSearch = false
            asset1  = filteredTableData[indexPath.row]as! NSDictionary
            let vc=self.storyboard?.instantiateViewController(withIdentifier: "MessageDetailViewController") as! MessageDetailViewController
            vc.messageDetailDictionary = asset1
            self.navigationController?.pushViewController(vc, animated: true)
            
        }
        else{
            
            let data = messageArrayData.object(at: indexPath.row) as! NSDictionary
            let asset = data.value(forKey: ASSET) as! NSDictionary
            let vc=self.storyboard?.instantiateViewController(withIdentifier: "MessageDetailViewController") as! MessageDetailViewController
            vc.messageDetailDictionary = asset
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }
    
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        
        if(self.tableView.contentOffset.y >= (self.tableView.contentSize.height - self.tableView.bounds.size.height))
        {
            if(isPageRefreshing==false){
                isPageRefreshing=true;
                print ("load more api")
                let number:CGFloat = CGFloat(totalPageCount/10)
                let roundedFloat = CGFloat(ceil(Double(number)))
                print("value is \(roundedFloat)")
                
                if(page <= Int(roundedFloat))
                {
                    page = page + 1
                    LoadMoreApiCall(page: page)
                }
                else
                {
                    
                    print("No more data")
                    
                }
                
            }
        }
    }
    
    // MARK:- CUSTOM FUNCTIONS
    // MESSAGE API
    func MessageApiCall(){
        if Connectivity.isConnectedToInternet() {
            SVProgressHUD.show()
            let parameters: Parameters = ["page" : "1" , "app_id" : APPIDVALUE]
            
            // for testing only
            if let userData = UserDefaults.standard.value(forKey: USERDATA) {
                let accessToken = (userData as AnyObject).value(forKey: ACCESS_TOKEN) as! String
                access_Token = accessToken
            }
            let headers: HTTPHeaders = [AUTHORIZATION: "\(BEARER)\(access_Token!)"]
            Alamofire.request("\(BASE_URL)\(GET_MESSAGE_API)", method: .post, parameters: parameters, encoding: JSONEncoding.default, headers:headers ).responseJSON { (response) in
                SVProgressHUD.dismiss()
                if response.result.value != nil
                {
                    let json = response.result.value! as! NSDictionary
                    print(json)
                    switch json[CODE] as! String
                    {
                    case "10":
                        self.messageArrayData = NSMutableArray(array: json.value(forKey: RESULT) as! NSArray)
                        if self.messageArrayData.count == 0
                        {
                            self.addAlertView(title: ALERT, message: json[MESSAGE]  as! String, buttonTitle: CLICKOK)
                        }
                        else
                        {
                            if self.refreshControl.isRefreshing
                            {
                                self.refreshControl.endRefreshing()
                            }
                            self.isPageRefreshing=false
                            self.totalPageCount = json.value(forKey: "total_data") as! CGFloat
                            print(self.totalPageCount)
                            self.tableView.reloadData()
                        }
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
                                        //  let addCardViewController = STPAddCardViewController()
                                        //  addCardViewController.delegate = self
                                        // Present add card view controller
                                        // self.navigationController?.pushViewController(addCardViewController, animated: true)
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
                                    self.MessageApiCall()
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
                    self.addAlertView(title: ALERT, message: SOMESERVERISSUE, buttonTitle: CLICKOK)
                }
            }
        }
        else{
            self.addAlertView(title: ALERT, message: SOMESERVERISSUE, buttonTitle: CLICKOK)
        }
    }
    
    func LoadMoreApiCall(page:Int){
        if Connectivity.isConnectedToInternet() {
            SVProgressHUD.show()
            
            let parameters: Parameters = ["page" : String(page) , "app_id" : APPIDVALUE]
            // for testing only
            if let userData = UserDefaults.standard.value(forKey: USERDATA) {
                let accessToken = (userData as AnyObject).value(forKey: ACCESS_TOKEN) as! String
                access_Token = accessToken
            }
            let headers: HTTPHeaders = [AUTHORIZATION: "\(BEARER)\(access_Token!)"]
            Alamofire.request("\(BASE_URL)\(GET_MESSAGE_API)", method: .post, parameters: parameters, encoding: JSONEncoding.default, headers:headers ).responseJSON { (response) in
                SVProgressHUD.dismiss()
                if response.result.value != nil
                {
                    let json = response.result.value! as! NSDictionary
                    print(json)
                    switch json[CODE] as! String
                    {
                    case "10":
                        self.NSMutableArrayUpdated = NSMutableArray(array: json.value(forKey: RESULT) as! NSArray)
                        if self.messageArrayData.count == 0
                        {
                            self.addAlertView(title: ALERT, message: json[MESSAGE]  as! String, buttonTitle: CLICKOK)
                        }
                        else
                        {
                            if self.refreshControl.isRefreshing
                            {
                                self.refreshControl.endRefreshing()
                            }
                            
                            self.tableView.reloadData()
                        }
                        self.isPageRefreshing=false
                        
                        self.messageArrayData.addObjects(from: (self.NSMutableArrayUpdated as! NSMutableArray) as! [Any])
                        
                        print("Message array \(self.messageArrayData.count)")
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
                                    UserDefaults.standard.set(true, forKey: UPDATE_SUBSCRIPTION_USER)
                                    let alert = UIAlertController(title: ALERT, message: jsonMsg, preferredStyle: UIAlertControllerStyle.alert)
                                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: { (action: UIAlertAction!) in
                                        UserDefaults.standard.set(false, forKey: USERLOGGEDIN)
                                        let alert = UIAlertController(title: ALERT, message: jsonMsg, preferredStyle: UIAlertControllerStyle.alert)
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
                                    self.MessageApiCall()
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
                    self.addAlertView(title: ALERT, message: SOMESERVERISSUE, buttonTitle: CLICKOK)
                }
            }
        }
        else{
            self.addAlertView(title: ALERT, message: SOMESERVERISSUE, buttonTitle: CLICKOK)
        }
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
    //MARK:- TEXTFIELD DELEGATE
    func textFieldShouldReturn(_ textField: UITextField) -> Bool
    {
        textField.returnKeyType = UIReturnKeyType.search
        textField.resignFirstResponder()
        return true
    }
    func handleSwipes(sender:UISwipeGestureRecognizer) {
        if (sender.direction == .right) {
            tabBarController?.selectedIndex = 2
            let vc=self.storyboard?.instantiateViewController(withIdentifier: MESSAGE_VIEW_CONTROLLER ) as! MessagesViewController
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }
    @IBAction func btnProfile(_ sender: Any) {
        
        let vc=self.storyboard?.instantiateViewController(withIdentifier: USER_PROFILE_VIEW_CONTROLLER) as! UserProfileView
        vc.IsComingFrom = "other"
        self.navigationController?.pushViewController(vc, animated: true)
    }
}
