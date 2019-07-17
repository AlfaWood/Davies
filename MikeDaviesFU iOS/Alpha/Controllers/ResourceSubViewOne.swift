//
//  ResourceSubViewOne.swift
//  Alpha
//
//  Created by Razan Nasir on 06/07/18.
//  Copyright © 2018 Stpl. All rights reserved.
//

import UIKit
import SVProgressHUD
import Alamofire
import AlamofireObjectMapper
import ObjectMapper

class ResourceSubViewOne: UIViewController , UITableViewDelegate, UITableViewDataSource , UISearchBarDelegate ,UITextFieldDelegate  {
    
    //OUTLETS
    @IBOutlet weak var tableView: UITableView!
    
    @IBOutlet weak var resourceSubTitle: UILabel!
    
    @IBOutlet var searchBar: UISearchBar!
    
    
    //VARIABLES
    
    var searchActive : Bool = false
    var filteredTableData:[String] = []
    var searchedArray:[String] = []
    var resourceContentArrayData : NSMutableArray = NSMutableArray()
    var refreshControl = UIRefreshControl()
    var contentDictionaryArray : NSMutableArray = NSMutableArray()
    var contentAsset : NSDictionary = NSDictionary()
    var resourceID : Int?
    var resourceTopic : String?
    var resourceSubTopicID: Int?
    var idTopic : Int = 0
    var topic : String = ""
    var subid : Int = 0
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        searchBar.delegate = self
        searchBar.setImage(UIImage(named: "search"), for: .search, state: .normal)
        
        let searchTextField: UITextField? = searchBar.value(forKey: "searchField") as? UITextField
        
        UITextField.appearance(whenContainedInInstancesOf: [UISearchBar.self]).font = UIFont(name: "HelveticaNeue-Regular", size: 14.0)
        
        if searchTextField!.responds(to: #selector(getter: UITextField.attributedPlaceholder)) {
            //  let attributeDict = [NSAttributedStringKey.foregroundColor: UIColor.bt_color(fromHex:"3C77BD", alpha: 1.0)]
            let attributeDict = [NSAttributedStringKey.foregroundColor: UIColor(hexString: HEX_COLOUR).withAlphaComponent(1.0)]
            searchTextField!.attributedPlaceholder = NSAttributedString(string: "Type Here to Search", attributes: (attributeDict as Any as! [NSAttributedStringKey : Any]) )
        }
        
        searchActive = false
        
        self.tableView.estimatedRowHeight = 20.0
        ResourceApiCallContent()
        self.refreshControl.addTarget(self, action: #selector(ResourceSubViewController.refresh(sender:)), for: UIControlEvents.valueChanged)
        self.tableView.addSubview(refreshControl)
        
        tableView.reloadData()
        self.navigationController?.navigationBar.tintColor = UIColor.init(hexString:HEX_COLOUR)
        let navBarColor = navigationController!.navigationBar
        navBarColor.barTintColor = UIColor.white
        
        self.navigationController?.navigationBar.titleTextAttributes =
            [NSAttributedStringKey.foregroundColor: tabTintColor,
             NSAttributedStringKey.font: UIFont(name: "HelveticaNeue-CondensedBold", size: 30)!]
        self.tabBarController?.tabBar.isHidden = true
        self.resourceSubTitle.text =  String(format:"%@",resourceTopic!)
        
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
        
        searchActive = false
        searchBar.text = ""
        
        if(contentDictionaryArray.count == 0)
        {
            ResourceApiCallContent()
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
    
    @objc func singleTap(sender: UITapGestureRecognizer) {
        self.searchBar.resignFirstResponder()
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
            return contentDictionaryArray.count
        }
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell  = tableView.dequeueReusableCell(withIdentifier: "resourceSubView", for: indexPath)
        
        if(searchActive)
        {
            (cell.contentView.viewWithTag(10) as! UILabel).text = filteredTableData[indexPath.row]
            
        }
        else
        {
            let data = contentDictionaryArray.object(at: indexPath.row) as! NSDictionary
            let asset = data.value(forKey: ASSET) as! NSDictionary
            (cell.contentView.viewWithTag(10) as! UILabel).text = asset.value(forKey: TITLE) as? String
        }
        return cell
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        
        if (searchActive)
        {
            print("filteredTableData: \(filteredTableData[indexPath.row])")
            var i = 0
            for asset in self.contentDictionaryArray
            {
                let assetD = asset as! NSDictionary
                let assetDic = assetD[ASSET] as! NSDictionary
                print("asset\(assetDic)")
                topic = assetDic["title"] as! String
                
                if topic == filteredTableData[indexPath.row]
                {
                    print(" value of i :\(i)")
                    
                    let data = contentDictionaryArray.object(at: i) as! NSDictionary
                    let asset = data.value(forKey: ASSET) as! NSDictionary
                    
                    let vc=self.storyboard?.instantiateViewController(withIdentifier: CONTENT_VIEW_CONTROLLER) as! ContentViewController
                    vc.contentAsset = asset
                    vc.contentDictionary = data
                    self.navigationController?.pushViewController(vc, animated: true)
                }
                i = i + 1
            }
            
        }
        else
        {
            
            let data = contentDictionaryArray.object(at: indexPath.row) as! NSDictionary
            let asset = data.value(forKey: ASSET) as! NSDictionary
            let vc=self.storyboard?.instantiateViewController(withIdentifier: CONTENT_VIEW_CONTROLLER) as! ContentViewController
            vc.contentAsset = asset
            vc.contentDictionary = data
            self.navigationController?.pushViewController(vc, animated: true)
        }
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
    
    
    
    @IBAction func backButton(_ sender: Any) {
        self.dismiss(animated: true, completion: nil)
    }
    
    @objc func refresh(sender:AnyObject){
        ResourceApiCallContent()
        tableView.reloadData()
    }
    
    // MARK:- CUSTOM FUNCTIONS
    
    //resource sub api
    
    
    // RESOURCE API
    
    
    func ResourceApiCallContent(){
        
        SVProgressHUD.show()
        if let userData = UserDefaults.standard.value(forKey: USERDATA) {
            let accessToken = (userData as AnyObject).value(forKey: ACCESS_TOKEN) as! String
            access_Token = accessToken
        }
        
        let parameters: Parameters = [ID: resourceID ?? ""]
        
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
                            print("json result array : \(String(describing: json.resultArray))")
                            self.contentDictionaryArray = NSMutableArray(array: json.resultArray!)
                            print("json result dict : \(self.contentDictionaryArray)")
                            
                            self.searchedArray.removeAll()
                            
                            for asset in self.contentDictionaryArray
                            {
                                let assetD = asset as! NSDictionary
                                let assetDic = assetD[ASSET] as! NSDictionary
                                print("asset\(assetDic)")
                                let topic = assetDic["title"] as! String
                                self.searchedArray.insert(topic, at: 0)
                                
                            }
                            
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
                        Singleton.sharedInstance.refreshToken(arg: true, completion: { (success,jsonCode,jsonMsg) -> Void in
                            print("Second line of code executed")
                            if success { // this will be equal to whatever value is set in this method call
                                self.ResourceApiCallContent()
                            } else {
                            }
                        }, failure: {(alert,message) -> Void in
                            self.addAlertView(title: alert, message: message, buttonTitle: CLICKOK)
                        }
                        )
                        break
                    case .none: break
                    case .some(_):
                        self.addAlertView(title: json.status!, message: json.msg!, buttonTitle: CLICKOK)
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
    
}
