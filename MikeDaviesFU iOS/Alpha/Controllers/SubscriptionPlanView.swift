//
//  SubscriptionPlanView.swift
//  StripeDemo
//
//  Created by Akash Verma on 6/8/18.
//  Copyright © 2018 Chandra Mouli Shukla. All rights reserved.
//

import UIKit
import AVFoundation
import AVKit
import Foundation
import AlamofireObjectMapper
import SVProgressHUD
import Alamofire
import ObjectMapper

class SubscriptionPlanView: UIViewController ,UITableViewDelegate,UITableViewDataSource {
    
    
    var userparams : NSMutableDictionary!
    var amount : String! = ""
    var planID : String! = ""
    
    @IBOutlet weak var tableView: UITableView!
    var subscriptionPlanArray: NSMutableArray = NSMutableArray()
    override func viewDidLoad() {
        super.viewDidLoad()
        
        tableView.delegate = self
        tableView.dataSource = self
        
        let defaults = UserDefaults.standard
        defaults.set(userparams[EMAIL], forKey:EMIALID)
        defaults.set(userparams[PASSWORD], forKey:USER_PASSWORD)
        
        self.getSubscriptionPalnData()
        
        // Do any additional setup after loading the view.
    }
    
    override func viewWillAppear(_ animated: Bool) {
        /*
         let navView = UIView()
         navView.frame =  CGRect(x:-350, y:-10, width:950, height:65)
         let image = UIImageView()
         image.image = UIImage(named: "DPE-Inline")
         image.frame = CGRect(x:-350, y:-10, width:950, height:65)
         image.contentMode = UIViewContentMode.scaleAspectFit
         // Add both the label and image view to the navView
         navView.addSubview(image)
         
         // Set the navigation bar's navigation item's titleView to the navView
         self.navigationItem.titleView = navView
         
         // Set the navView's frame to fit within the titleView
         navView.sizeToFit()
         */
        
        updateNavigationBarUI()
        
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
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return subscriptionPlanArray.count
        
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 150
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "MyCell", for: indexPath)
        
        let maincellView = cell.contentView.viewWithTag(5)
        
        let lblMonth = cell.contentView.viewWithTag(1) as? UILabel
        let lblCost = cell.contentView.viewWithTag(2) as? UILabel
        let lblMonthlySubscription = cell.contentView.viewWithTag(3) as? UILabel
        let subscribeBnt = cell.contentView.viewWithTag(4) as? UIButton
        
        let dict = subscriptionPlanArray.object(at: indexPath.row) as! NSDictionary
        
        if let strMonth = dict.value(forKey:"name") as? String
        {
            lblMonth?.text = strMonth
        }
        if let strMonthlySubscription = dict.value(forKey:"statement_descriptor") as? String
        {
            lblMonthlySubscription?.text = strMonthlySubscription
        }
        if let strCost = dict.value(forKey:"amount")
        {
            lblCost?.text =  String(format: "$%@", strCost as! CVarArg)
        }
        
        subscribeBnt?.tag = indexPath.row
        
        let myColor : UIColor = UIColor.init(hexString:HEX_COLOUR)
        
        maincellView?.layer.cornerRadius = 5;
        maincellView?.layer.masksToBounds = true;
        maincellView?.layer.borderColor = myColor.cgColor;
        maincellView?.layer.borderWidth = 2.0;
        subscribeBnt?.addTarget(self, action: #selector(buttonClicked), for: .touchUpInside)
        
        return cell
    }
    @IBAction func back(_ sender: Any) {
        self.navigationController?.popViewController(animated: true)
    }
    @objc func buttonClicked(sender:UIButton)
    {
        self.navigationController?.setNavigationBarHidden(false, animated: false)
        let dict = subscriptionPlanArray.object(at: sender.tag) as! NSDictionary
        amount = String(format: "%@", dict["amount"] as! CVarArg)
        planID = dict["product"] as! String
        print("amount is --- \(String(describing: amount))")
        print("plan ID is --- \(String(describing: planID))")
        print("amount is with square --- \(dict["amount"]!)")
        
    }
    
    func getSubscriptionPalnData()
    {
        
        let defaults = UserDefaults.standard
        if let accessToken = defaults.string(forKey: "accessToken") {
            access_Token = accessToken
        }
        self.tabBarController?.tabBar.isHidden = true
        
        SVProgressHUD.show()
        
        let parameters: Parameters = [APPID:APPIDVALUE]
        
        Alamofire.request("\(BASE_URL)\(getPlanList)", method: .post, parameters: parameters , encoding: JSONEncoding.default, headers:nil ).responseObject { (response:DataResponse<User>)  in
            debugPrint("response: \(response)")
            
            if Connectivity.isConnectedToInternet() {
                if response.result.value != nil
                {
                    SVProgressHUD.dismiss()
                    let json = response.result.value!
                    switch json.code
                    {
                    case "10"?:
                        self.subscriptionPlanArray = NSMutableArray(array: json.resultArray!)
                        print("self.SubscriptionArrayData: \(self.subscriptionPlanArray)")
                        
                        self.tableView.reloadData()
                        break
                    case "0"?:
                        self.addAlertView(title: json.status!, message: json.msg!, buttonTitle: CLICKOK)
                    case "1"?:
                        self.addAlertView(title: json.status!, message: json.msg!, buttonTitle: CLICKOK)
                    case "401"?:
                        Singleton.sharedInstance.refreshToken(arg: true, completion: {(success,jsonCode,jsonMsg) -> Void in
                            print("Second line of code executed")
                            if success {
                                
                                self.getSubscriptionPalnData()
                            } else {
                            }
                        }, failure: {(alert,message) -> Void in
                            self.addAlertView(title: alert, message: message, buttonTitle: CLICKOK)
                        }
                        )
                        break
                    case .none:
                        Singleton.sharedInstance.refreshToken(arg: true, completion: { (success,jsonCode,jsonMsg)-> Void in
                            print("Second line of code executed")
                            if success { // this will be equal to whatever value is set in this method call
                                self.getSubscriptionPalnData()
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
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
}
