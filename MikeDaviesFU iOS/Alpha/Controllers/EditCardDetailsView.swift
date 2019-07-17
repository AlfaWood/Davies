//
//  EditCardDetailsView.swift
//  Alpha
//
//  Created by Akash Verma on 6/12/18.
//  Copyright © 2018 Stpl. All rights reserved.
//

import UIKit

import Foundation

import SVProgressHUD
import Alamofire
import AlamofireObjectMapper
import ObjectMapper

class EditCardDetailsView: UIViewController,UITableViewDataSource,UITableViewDelegate {
    
    var cardDetailsData: NSMutableArray = NSMutableArray()
    
    var strCardID = String()
    var strCustomerId = String()
    
    @IBOutlet weak var tableView: UITableView!
    
    override func viewDidLoad() {
        
        super.viewDidLoad()
        self.getCardDetailsDataData()
        
        // Do any additional setup after loading the view.
    }
    
    override func viewWillAppear(_ animated: Bool) {
        /*
         let navView = UIView()
         navView.frame =  CGRect(x: -100, y: -5, width:450, height:35)
         // Create the image view
         let image = UIImageView()
         image.image = UIImage(named: "DPE-Inline")
         // To maintain the image's aspect ratio:
         // Setting the image frame so that it's immediately before the text:
         image.frame = CGRect(x: -100, y: -5, width:450, height:35)
         image.contentMode = UIViewContentMode.scaleAspectFit
         
         // Add both the label and image view to the navView
         navView.addSubview(image)
         
         // Set the navigation bar's navigation item's titleView to the navView
         self.navigationItem.titleView = navView
         
         // Set the navView's frame to fit within the titleView
         navView.sizeToFit()
         */
        
        updateNavigationBarUI()
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
        return cardDetailsData.count
        
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 90
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "MyCell", for: indexPath)
        
        let viewMain = cell.contentView.viewWithTag(1)
        let lblCard = cell.contentView.viewWithTag(2) as! UILabel
        let lblExp = cell.contentView.viewWithTag(3) as! UILabel
        
        let deleteBtn = cell.contentView.viewWithTag(4) as! UIButton
        
        viewMain?.layer.cornerRadius = 10;
        viewMain?.layer.masksToBounds = true;
        viewMain?.layer.borderColor = UIColor.lightGray.cgColor;
        viewMain?.layer.borderWidth = 1.0;
        
        
        if let dict = cardDetailsData.object(at: indexPath.row) as? NSDictionary
        {
            lblCard.text = String(format: "****  ****  **** %@",(dict.value(forKey: "last4") as! String))
            let strMonth = dict.value(forKey: "exp_month") as! String
            let strYear = dict.value(forKey: "exp_year") as! String
            let  splitStrYear = strYear.suffix(2)
            let strSlash = "\\"
            let  strExp  = "\(strMonth)\(strSlash)\(splitStrYear)"
            
            lblExp.text = strExp
            print(strExp)
        }
        
        
        return cell
    }
    
    
    
    func getCardDetailsDataData()
    {
        SVProgressHUD.show()
        let defaults = UserDefaults.standard
        if let accessToken = defaults.string(forKey: "accessToken") {
            access_Token = accessToken
        }
        self.tabBarController?.tabBar.isHidden = true
        
        let headers: HTTPHeaders = ["Authorization": "\("Bearer ")\(access_Token!)"]
        
        Alamofire.request("\(BASE_URL)\(GET_CARD_List)", method: .get, parameters: nil , encoding: JSONEncoding.default, headers:headers ).responseObject { (response:DataResponse<User>)  in
            debugPrint("response: \(response)")
            SVProgressHUD.dismiss()
            
            if Connectivity.isConnectedToInternet() {
                if response.result.value != nil
                {
                    SVProgressHUD.dismiss()
                    let json = response.result.value!
                    switch json.code{
                    case "10"?:
                        self.cardDetailsData = NSMutableArray(array: json.resultArray!)
                        print("self.cardDetails: \(self.cardDetailsData)")
                        
                        self.tabBarController?.tabBar.isHidden = true
                        self.navigationController?.setNavigationBarHidden(false, animated: false)
                        
                        let defaults = UserDefaults.standard
                        defaults.set(false, forKey: APP_LAUNCH)
                        
                        self.tableView.reloadData()
                        break
                    case "0"?:
                        self.addAlertView(title: json.status!, message: json.msg!, buttonTitle: CLICKOK)
                    case "1"?:
                        self.addAlertView(title: json.status!, message: json.msg!, buttonTitle: CLICKOK)
                    case "401"?:
                        Singleton.sharedInstance.refreshToken(arg: true, completion: { (success,jsonCode,jsonMsg) -> Void in
                            if success {
                                
                                self.getCardDetailsDataData()
                            } else {
                            }
                        }, failure: {(alert,message) -> Void in
                            self.addAlertView(title: alert, message: message, buttonTitle: CLICKOK)
                        }
                        )
                        break
                    case .none:
                        Singleton.sharedInstance.refreshToken(arg: true, completion: { (success,jsonCode,jsonMsg) -> Void in
                            print("Second line of code executed")
                            if success { // this will be equal to whatever value is set in this method call
                                self.getCardDetailsDataData()
                            } else {
                            }
                        }, failure: {(alert,message) -> Void in
                            self.addAlertView(title: alert, message: message, buttonTitle: CLICKOK)
                        }
                        )
                        break
                    case .some(_): break
                    }
                    /*  if self.cardDetailsData.count == 1 {
                     let alertController = UIAlertController(title: ALERT,
                     message: DELETE_CARD_ALERT_LASTCARD,
                     preferredStyle: .alert)
                     let alertAction = UIAlertAction(title: "OK", style: .default, handler: { _ in
                     self.navigationController?.popViewController(animated: true)
                     })
                     alertController.addAction(alertAction)
                     self.present(alertController, animated: true)
                     }
                     */
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
    
    func deleteCardDetailsData()
    {
        
        let defaults = UserDefaults.standard
        if let accessToken = defaults.string(forKey: "accessToken") {
            access_Token = accessToken
        }
        self.tabBarController?.tabBar.isHidden = true
        
        let headers: HTTPHeaders = ["Authorization": "\("Bearer ")\(access_Token!)"]
        
        let parameters1: Parameters = ["customer_id":strCustomerId ,"card_id": strCardID]
        
        
        
        Alamofire.request("\(BASE_URL)\(DELETE_CARD)", method: .post , parameters: parameters1 , encoding: JSONEncoding.default, headers:headers ).responseObject { (response:DataResponse<User>)  in
            SVProgressHUD.dismiss()
            debugPrint("response: \(response)")
            
            if Connectivity.isConnectedToInternet() {
                if response.result.value != nil
                {
                    
                    let json = response.result.value!
                    switch json.code{
                    case "10"?:
                        
                        let defaults = UserDefaults.standard
                        defaults.set(false, forKey: APP_LAUNCH)
                        let alertController = UIAlertController(title: json.status!, message: cardDeleteMessage, preferredStyle: .alert)
                        // Create the actions
                        let okAction = UIAlertAction(title: "OK", style: UIAlertActionStyle.default) {
                            UIAlertAction in
                            self.getCardDetailsDataData()
                        }
                        // Add the actions
                        alertController.addAction(okAction)
                        // Present the controller
                        self.present(alertController, animated: true, completion: nil)
                        self.tableView.reloadData()
                        break
                    case "0"?:
                        self.addAlertView(title: json.status!, message: json.msg!, buttonTitle: CLICKOK)
                    case "1"?:
                        self.addAlertView(title: json.status!, message: json.msg!, buttonTitle: CLICKOK)
                    case "401"?:
                        Singleton.sharedInstance.refreshToken(arg: true, completion: { (success,jsonCode,jsonMsg) -> Void in
                            print("Second line of code executed")
                            if success {
                                
                                self.getCardDetailsDataData()
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
                                self.getCardDetailsDataData()
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
    
    
    
    
    @IBAction func btnDelete(_ sender: Any) {
        
        if cardDetailsData.count == 1 {
            
            self.addAlertView(title:ALERT, message: DELETE_CARD_ALERT_LASTCARD, buttonTitle: CLICKOK)
        }
        else
        {
            let alertController = UIAlertController(title: "",
                                                    message: DELETE_CARD_ALERT,
                                                    preferredStyle: .alert)
            let alertAction = UIAlertAction(title: "YES", style: .default, handler: { _ in
                let buttonPosition = (sender as AnyObject).convert(CGPoint.zero, to: self.tableView)
                let indexPath = self.tableView.indexPathForRow(at: buttonPosition)
                let data:NSDictionary = self.cardDetailsData[(indexPath?.row)!] as! NSDictionary
                self.self.strCardID = data.value(forKey: "card_id") as! String
                self.strCustomerId = data.value(forKey: "customer") as! String
                self.deleteCardDetailsData()
                SVProgressHUD.show()
            })
            let alertActionCancel = UIAlertAction(title: "NO", style: .cancel, handler: { _ in
                
            })
            alertController.addAction(alertAction)
            alertController.addAction(alertActionCancel)
            self.present(alertController, animated: true)
        }
        
    }
    @IBAction func backBtnAction(_ sender: Any) {
        
        if let navController = self.navigationController {
            navController.popViewController(animated: true)
        }
    }
    
}
