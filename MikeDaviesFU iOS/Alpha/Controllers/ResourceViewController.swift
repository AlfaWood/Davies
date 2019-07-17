//
//  ResourceViewController.swift
//  Alpha
//
//  Created by Razan Nasir on 13/04/18.
//  Copyright © 2018 Stpl. All rights reserved.


import UIKit
import SVProgressHUD
import Alamofire
import AlamofireObjectMapper
import ObjectMapper
import SDWebImage

class ResourceViewController: UIViewController,TabBarSwitcher,UITableViewDelegate, UITableViewDataSource,UISearchBarDelegate,UITextFieldDelegate
{
    
    @IBOutlet weak var profileImgBtn: UIButton!
    
    //OUTLETS
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var searchBar: UISearchBar!
    var refreshControl = UIRefreshControl()
    
    // VARIABLES
    var filteredTableData:[String] = []
    var searchedArray:[String] = []
    var resourceArrayData = NSArray()
    var searchActive : Bool = false
    var idTopic : Int = 0
    var topic : String = ""
    var subid : Int = 0
    
    override func viewDidLoad() {
        super.viewDidLoad()
        searchBar.delegate = self
        searchBar.setImage(UIImage(named: "search"), for: .search, state: .normal)
        UITextField.appearance(whenContainedInInstancesOf: [UISearchBar.self]).font = UIFont(name: "HelveticaNeue-Regular", size: 14.0)
        self.refreshControl.addTarget(self, action: #selector(ResourceViewController.refresh(sender:)), for: UIControlEvents.valueChanged)
        self.tableView.addSubview(refreshControl)
        
        let searchTextField: UITextField? = searchBar.value(forKey: "searchField") as? UITextField
        if searchTextField!.responds(to: #selector(getter: UITextField.attributedPlaceholder)) {
            //  let attributeDict = [NSAttributedStringKey.foregroundColor: UIColor.bt_color(fromHex:"3C77BD", alpha: 1.0)]
            let attributeDict = [NSAttributedStringKey.foregroundColor: UIColor(hexString: HEX_COLOUR).withAlphaComponent(1.0)]
            searchTextField!.attributedPlaceholder = NSAttributedString(string: "Type Here to Search", attributes: (attributeDict as Any as! [NSAttributedStringKey : Any]) )
        }
        
        
        self.navigationController?.navigationBar.tintColor = UIColor.init(hexString:HEX_COLOUR)
        let navBarColor = navigationController!.navigationBar
        navBarColor.barTintColor = UIColor.white
        
        self.navigationController?.navigationBar.titleTextAttributes =
            [NSAttributedStringKey.foregroundColor: tabTintColor,
             NSAttributedStringKey.font: UIFont(name: "HelveticaNeue-CondensedBold", size: 30)!]
        initSwipe(direction: .left)
        initSwipe(direction: .right)
        self.tableView.estimatedRowHeight = 20.0
        searchActive = false
        ResourceApiCall()
        let singleTapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(self.singleTap(sender:)))
        singleTapGestureRecognizer.numberOfTapsRequired = 1
        singleTapGestureRecognizer.isEnabled = true
        singleTapGestureRecognizer.cancelsTouchesInView = false
        self.view.addGestureRecognizer(singleTapGestureRecognizer)
        
    }
    @objc func singleTap(sender: UITapGestureRecognizer) {
        self.searchBar.resignFirstResponder()
    }
    override func viewWillAppear(_ animated: Bool) {
        /*
         let navView = UIView()
         navView.frame =  CGRect(x:-350, y:-10, width:950, height:65)
         let image = UIImageView()
         image.image = UIImage(named: "DPE-Inline")
         image.frame = CGRect(x:-350, y:-10, width:950, height:65)
         image.contentMode = UIViewContentMode.scaleAspectFit
         navView.sizeToFit()
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
        
        searchBar.text = ""
        searchActive = false
        ResourceApiCall()
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
        if(resourceArrayData.count == 0)
        {
            ResourceApiCall()
        }
        tableView.reloadData()
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
    
    func searchBarTextDidBeginEditing(_ searchBar: UISearchBar) {
    }
    
    func searchBarTextDidEndEditing(_ searchBar: UISearchBar) {
    }
    
    
    
    //Search Method
    
    func searchBarCancelButtonClicked(_ searchBar: UISearchBar) {
        self.tableView.isHidden = false
    }
    
    func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
        self.searchBar.endEditing(true)
    }
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {
        
        filteredTableData.removeAll()
        filteredTableData = searchedArray.filter({ (topic) -> Bool in
            let tmp: NSString = topic as NSString
            let range = tmp.range(of: searchText, options: NSString.CompareOptions.caseInsensitive)
            return range.location != NSNotFound
        })
        print("filter array :\(filteredTableData)")
        if(filteredTableData.count == 0){
            if searchBar.text != ""
            {
                print("No Data")
                self.tableView.isHidden = true
            }
            else
            {
                self.tableView.isHidden = false
                self.searchBar.endEditing(true)
            }
            searchActive = false;
        } else {
            searchActive = true;
            self.tableView.isHidden = false
        }
        self.tableView.reloadData()
        
        
    }
    
    //MARK:- TABLE VIEW DELEGATES AND DATASOURCE
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if (searchActive) {
            return filteredTableData.count
        }
        else {
            return resourceArrayData.count
        }
    }
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell  = tableView.dequeueReusableCell(withIdentifier: "resource", for: indexPath)
        if(searchActive){
            // cell.textLabel?.text = filteredTableData[indexPath.row] as? String;
            (cell.contentView.viewWithTag(10) as? UILabel)?.text = filteredTableData[indexPath.row]
            
        } else {
            
            let data = resourceArrayData.object(at: indexPath.row) as! NSDictionary
            let asset = data.value(forKey: "asset") as! NSDictionary
            //  cell.textLabel?.text = resourceArrayData[indexPath.row] as? String;
            (cell.contentView.viewWithTag(10) as! UILabel).text =
                asset["topic"] as? String
        }
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        if(searchActive){
            print("filteredTableData: \(filteredTableData[indexPath.row])")
            var i = 0
            for asset in self.resourceArrayData
            {
                let assetD = asset as! NSDictionary
                let assetDic = assetD[ASSET] as! NSDictionary
                print("asset\(assetDic)")
                topic = assetDic["topic"] as! String
                if topic == filteredTableData[indexPath.row]
                {
                    print(" value of i :\(i)")
                    
                    let data = resourceArrayData.object(at: i) as! NSDictionary
                    let asset = data.value(forKey: ASSET) as! NSDictionary
                    idTopic = asset.value(forKey: ID) as! Int
                    subid = asset.value(forKey: SUB_ID) as! Int
                    
                    if (subid == 1)
                    {
                        let vc=self.storyboard?.instantiateViewController(withIdentifier: RESOURCE_SUBVIEW_VIEW_CONTROLLER) as! ResourceSubViewController
                        vc.resourceID = idTopic
                        vc.resourceTopic = topic
                        vc.resourceSubTopicID = subid
                        self.navigationController?.pushViewController(vc, animated: true)
                    }
                    else
                    {
                        ResourceApiCallContent()
                    }
                    
                }
                i=i+1
            }
            
        }else{
            let data = resourceArrayData.object(at: indexPath.row) as! NSDictionary
            let asset = data.value(forKey: ASSET) as! NSDictionary
            idTopic = asset.value(forKey: ID) as! Int
            topic = asset.value(forKey: TOPIC) as! String
            let subid = asset.value(forKey: SUB_ID) as! Int
            
            if (subid == 1)
            {
                let vc=self.storyboard?.instantiateViewController(withIdentifier: RESOURCE_SUBVIEW_VIEW_CONTROLLER) as! ResourceSubViewController
                vc.resourceID = idTopic
                vc.resourceTopic = topic
                vc.resourceSubTopicID = subid
                self.navigationController?.pushViewController(vc, animated: true)
            }
            else
            {
                ResourceApiCallContent()
                
            }
        }
    }
    
    @objc func refresh(sender:AnyObject){
        ResourceApiCall()
        searchBar.text = ""
        searchActive = false
        tableView.reloadData()
    }
    // MARK:- CUSTOM FUNCTIONS
    // RESOURCE API
    func ResourceApiCall(){
        SVProgressHUD.show()
        if let userData = UserDefaults.standard.value(forKey: USERDATA) {
            let accessToken = (userData as AnyObject).value(forKey: ACCESS_TOKEN) as! String
            access_Token = accessToken
        }
        
        let parameters: Parameters = ["app_id" : APPIDVALUE]
        let headers: HTTPHeaders = ["Authorization": "\("Bearer ")\(access_Token!)"]
        Alamofire.request("\(BASE_URL)\(RESOURCE_API)", method: .post , parameters: parameters , encoding: JSONEncoding.default, headers:headers ).responseObject { (response:DataResponse<User>)  in
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
                            self.addAlertView(title: ALERT, message: json.msg!, buttonTitle: CLICKOK)
                        }
                        else
                        {
                            self.resourceArrayData = NSMutableArray(array: json.resultArray!)
                            print("self.resourceArrayData: \(self.resourceArrayData)")
                            self.searchedArray.removeAll()
                            for asset in self.resourceArrayData
                            {
                                let assetD = asset as! NSDictionary
                                let assetDic = assetD[ASSET] as! NSDictionary
                                print("asset\(assetDic)")
                                let topic = assetDic["topic"] as! String
                                self.searchedArray.insert(topic, at: 0)
                                
                            }
                            
                            if self.refreshControl.isRefreshing
                            {
                                self.refreshControl.endRefreshing()
                            }
                            
                            self.tableView.reloadData()
                            print("searchedArray\(self.searchedArray)")
                            
                        }
                        break
                    case "0"?:
                        self.addAlertView(title: json.status!, message: json.msg!, buttonTitle: CLICKOK)
                    case "1"?:
                        self.addAlertView(title: json.status!, message: json.msg!, buttonTitle: CLICKOK)
                    case "401"?:
                        Singleton.sharedInstance.refreshToken(arg: true, completion: { (success,jsonCode,jsonMsg) -> Void in
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
                                    self.ResourceApiCall()
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
                    //                    self.addAlertView(title: ALERT, message: SOMESERVERISSUE, buttonTitle: CLICKOK)
                }
            }
            else{
                self.addAlertView(title: ALERT, message: SOMESERVERISSUE, buttonTitle: CLICKOK)
            }
        }
    }
    
    func ResourceApiCallContent(){
        
        SVProgressHUD.show()
        if let userData = UserDefaults.standard.value(forKey: USERDATA) {
            let accessToken = (userData as AnyObject).value(forKey: ACCESS_TOKEN) as! String
            access_Token = accessToken
        }
        
        let parameters: Parameters = [ID: idTopic ]
        
        let headers: HTTPHeaders = ["Authorization": "\("Bearer ")\(access_Token!)"]
        Alamofire.request("\(BASE_URL)\(RESOURCE_CONTENT_API)", method: .post, parameters: parameters , encoding: JSONEncoding.default, headers:headers ).responseObject { (response:DataResponse<User>)  in
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
                            self.addAlertView(title: ALERT, message: json.msg!, buttonTitle: CLICKOK)
                        }
                        else
                        {
                            let vc=self.storyboard?.instantiateViewController(withIdentifier: RESOURCE_SUBVIEW_VIEW_CONTROLLER_ONE) as! ResourceSubViewOne
                            vc.resourceID = self.idTopic
                            vc.resourceTopic = self.topic
                            vc.resourceSubTopicID = self.subid
                            self.navigationController?.pushViewController(vc, animated: true)
                        }
                        break
                    case "0"?:
                        self.addAlertView(title: json.status!, message: json.msg!, buttonTitle: CLICKOK)
                    case "1"?:
                        self.addAlertView(title: json.status!, message: json.msg!, buttonTitle: CLICKOK)
                    case "401"?:
                        Singleton.sharedInstance.refreshToken(arg: true, completion: { (success,jsonCode,jsonMsg) -> Void in
                            print("Second line of code executed")
                            if success {
                                if jsonCode == "4"
                                {
                                    let alert = UIAlertController(title: ALERT, message: jsonMsg, preferredStyle: UIAlertControllerStyle.alert)
                                    alert.addAction(UIAlertAction(title: "Ok", style: .default, handler: { (action: UIAlertAction!) in
                                        UserDefaults.standard.set(false, forKey: USERLOGGEDIN)
                                        let vc=self.storyboard?.instantiateViewController(withIdentifier: SUBSCRIPTION_PLAN) as! MasterViewController
                                        self.navigationController?.pushViewController(vc, animated: true)
                                        //   let addCardViewController = STPAddCardViewController()
                                        //     addCardViewController.delegate = self
                                        // Present add card view controller
                                        // self.navigationController?.pushViewController(addCardViewController, animated: true)
                                        self.tabBarController?.tabBar.isHidden = true
                                        
                                    }))
                                    self.present(alert, animated: true, completion: nil)
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
                                else
                                {
                                    self.ResourceApiCallContent()
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
        textField.returnKeyType = UIReturnKeyType.search
        textField.resignFirstResponder()
        return true
    }
    func handleSwipes(sender:UISwipeGestureRecognizer) {
        if (sender.direction == .right) {
            tabBarController?.selectedIndex = 0
        }
        else  if (sender.direction == .left) {
            tabBarController?.selectedIndex = 2
            let vc=self.storyboard?.instantiateViewController(withIdentifier: QA_VIEW_CONTROLLER ) as! QAViewController
            show(vc, sender: nil)
        }
    }
    
    
    @IBAction func btnProfile(_ sender: Any) {
        
        let vc=self.storyboard?.instantiateViewController(withIdentifier: USER_PROFILE_VIEW_CONTROLLER) as! UserProfileView
        vc.IsComingFrom = "other"
        self.navigationController?.pushViewController(vc, animated: true)
    }
    
}

