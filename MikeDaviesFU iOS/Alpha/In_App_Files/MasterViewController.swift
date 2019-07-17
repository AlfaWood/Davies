/// Copyright (c) 2018 Razeware LLC
///
/// Permission is hereby granted, free of charge, to any person obtaining a copy
/// of this software and associated documentation files (the "Software"), to deal
/// in the Software without restriction, including without limitation the rights
/// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
/// copies of the Software, and to permit persons to whom the Software is
/// furnished to do so, subject to the following conditions:
///
/// The above copyright notice and this permission notice shall be included in
/// all copies or substantial portions of the Software.
///
/// Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
/// distribute, sublicense, create a derivative work, and/or sell copies of the
/// Software in any work that is designed, intended, or marketed for pedagogical or
/// instructional purposes related to programming, coding, application development,
/// or information technology.  Permission for such use, copying, modification,
/// merger, publication, distribution, sublicensing, creation of derivative works,
/// or sale is expressly withheld.
///
/// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
/// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
/// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
/// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
/// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
/// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
/// THE SOFTWARE.

import UIKit
import StoreKit
import SVProgressHUD

class MasterViewController: UIViewController,UITableViewDelegate,UITableViewDataSource {
    @IBOutlet weak var navHeaderLbl: UILabel!
    @IBOutlet var tableView: UITableView!
    var products: [SKProduct] = []
    var userparams : NSMutableDictionary?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //        RazeFaceProducts.store.isProductPurchased(product.productIdentifier)
        SVProgressHUD.show()
        print("User Param : \(String(describing: userparams))!")
        
        UserDefaults.standard.set(userparams, forKey: "cardDetailsInfo")
        
        print("getDetails : \(String(describing: UserDefaults.standard.value(forKey: "cardDetailsInfo")))")
        
        let restoreButton = UIBarButtonItem(title: "Restore",
                                            style: .plain,
                                            target: self,
                                            action: #selector(MasterViewController.restoreTapped(_:)))
        navigationItem.rightBarButtonItem = restoreButton
        
        NotificationCenter.default.addObserver(self, selector: #selector(MasterViewController.handlePurchaseNotification(_:)),
                                               name: .IAPHelperPurchaseNotification,
                                               object: nil)
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        reload()
    }
    
    @objc func reload() {
        products = []
        tableView.reloadData()
        RazeFaceProducts.store.requestProducts{ [weak self] success, products in
            guard case self = self else { return }
            if success {
                SVProgressHUD.dismiss()
                self?.products = products!
                self?.tableView.reloadData()
            }
            else{
                SVProgressHUD.dismiss()
                self?.addAlertView(title: ALERT, message:"Cannot connect to iTunes Store", buttonTitle: CLICKOK)
            }
        }
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return products.count
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 150
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath) as! ProductCell
        
        let myColor : UIColor = UIColor.init(hexString:HEX_COLOUR)
        let maincellView = cell.contentView.viewWithTag(5)
        maincellView?.layer.cornerRadius = 5;
        maincellView?.layer.masksToBounds = true;
        maincellView?.layer.borderColor = myColor.cgColor;
        maincellView?.layer.borderWidth = 2.0;
        let product = products[indexPath.row]
        cell.product = product
        cell.buyButtonHandler =
            {
                product in
                RazeFaceProducts.store.buyProduct(product)
        }
        
        
        /*  let cell = tableView.dequeueReusableCell(withIdentifier: "MyCell", for: indexPath)
         
         
         let lblMonth = cell.contentView.viewWithTag(1) as? UILabel
         let lblCost = cell.contentView.viewWithTag(2) as? UILabel
         let lblMonthlySubscription = cell.contentView.viewWithTag(3) as? UILabel
         let subscribeBnt = cell.contentView.viewWithTag(4) as? UIButton
         
         subscribeBnt?.tag = indexPath.row
         
         let myColor : UIColor = tabTintColor
         
         maincellView?.layer.cornerRadius = 5;
         maincellView?.layer.masksToBounds = true;
         maincellView?.layer.borderColor = myColor.cgColor;
         maincellView?.layer.borderWidth = 2.0;
         //        subscribeBnt?.addTarget(self, action: #selector(buttonClicked), for: .touchUpInside)
         
         */
        return cell
    }
    
    @objc func restoreTapped(_ sender: AnyObject) {
        RazeFaceProducts.store.restorePurchases()
    }
    
    @objc func handlePurchaseNotification(_ notification: Notification) {
        SVProgressHUD.dismiss()
        if notification.object! as! String == IN_APP_PRODUCT_ID  {
            navigateToFeedScreen()
            //      let index = products.index(where: { product -> Bool in
            //        product.productIdentifier == productID
        }
        else {
            self.addAlertView(title: ALERT, message: notification.object! as! String, buttonTitle: CLICKOK)
            SVProgressHUD.dismiss()
            return
        }
        SVProgressHUD.dismiss()
    }
    func navigateToFeedScreen(){
        let appDelegate = UIApplication.shared.delegate as! AppDelegate
        appDelegate.feedViewOpen()
    }
}

// MARK: - UITableViewDataSource


