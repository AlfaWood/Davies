

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

import StoreKit
import Alamofire
import SVProgressHUD


public typealias ProductIdentifier = String
var recieptData   = NSString()
public typealias ProductsRequestCompletionHandler = (_ success: Bool, _ products: [SKProduct]?) -> Void

extension Notification.Name {
    static let IAPHelperPurchaseNotification = Notification.Name("IAPHelperPurchaseNotification")
}

open class IAPHelper: NSObject  {
    
    private let productIdentifiers: Set<ProductIdentifier>
    private var purchasedProductIdentifiers: Set<ProductIdentifier> = []
    private var productsRequest: SKProductsRequest?
    private var productsRequestCompletionHandler: ProductsRequestCompletionHandler?
    
    public init(productIds: Set<ProductIdentifier>) {
        productIdentifiers = productIds
        for productIdentifier in productIds {
            let purchased = UserDefaults.standard.bool(forKey: productIdentifier)
            if purchased {
                purchasedProductIdentifiers.insert(productIdentifier)
                print("Previously purchased: \(productIdentifier)")
            } else {
                print("Not purchased: \(productIdentifier)")
            }
        }
        super.init()
        
        SKPaymentQueue.default().add(self)
    }
}

// MARK: - StoreKit API

extension IAPHelper {
    
    public func requestProducts(_ completionHandler: @escaping ProductsRequestCompletionHandler) {
        productsRequest?.cancel()
        productsRequestCompletionHandler = completionHandler
        
        productsRequest = SKProductsRequest(productIdentifiers: productIdentifiers)
        productsRequest!.delegate = self
        productsRequest!.start()
    }
    
    public func buyProduct(_ product: SKProduct) {
        receiptValidation()
        print("Buying \(product.productIdentifier)...")
        let payment = SKPayment(product: product)
        SKPaymentQueue.default().add(payment)
    }
    
    func receiptValidation() {
        let SUBSCRIPTION_SECRET = "3d4dcf11d1eb4f4cb00b1601bb492954"
        let receiptPath = Bundle.main.appStoreReceiptURL?.path
        if FileManager.default.fileExists(atPath: receiptPath!){
            var receiptData:NSData?
            do{
                receiptData = try NSData(contentsOf: Bundle.main.appStoreReceiptURL!, options: NSData.ReadingOptions.alwaysMapped)
            }
            catch{
                print("ERROR: " + error.localizedDescription)
            }
            //let receiptString = receiptData?.base64EncodedString(options: NSData.Base64EncodingOptions(rawValue: 0))
            let base64encodedReceipt = receiptData?.base64EncodedString(options: NSData.Base64EncodingOptions.endLineWithCarriageReturn)
            
            recieptData = base64encodedReceipt! as NSString
            print("base 64 data -->> \(base64encodedReceipt!)")
            let requestDictionary = ["receipt-data":base64encodedReceipt!,"password":SUBSCRIPTION_SECRET]
            
            guard JSONSerialization.isValidJSONObject(requestDictionary) else {  print("requestDictionary is not valid JSON");  return }
            do {
                let requestData = try JSONSerialization.data(withJSONObject: requestDictionary)
                let validationURLString = "https://sandbox.itunes.apple.com/verifyReceipt"  // this works but as noted above it's best to use your own trusted server
                guard let validationURL = URL(string: validationURLString) else { print("the validation url could not be created, unlikely error"); return }
                let session = URLSession(configuration: URLSessionConfiguration.default)
                var request = URLRequest(url: validationURL)
                request.httpMethod = "POST"
                request.cachePolicy = URLRequest.CachePolicy.reloadIgnoringCacheData
                let task = session.uploadTask(with: request, from: requestData) { (data, response, error) in
                    if let data = data , error == nil {
                        do {
                            let appReceiptJSON = try JSONSerialization.jsonObject(with: data)
                            print("success. here is the json representation of the app receipt: \(appReceiptJSON)")
                            // if you are using your server this will be a json representation of whatever your server provided
                        } catch let error as NSError {
                            print("json serialization failed with error: \(error)")
                        }
                    } else {
                        print("the upload task returned an error: \(String(describing: error))")
                    }
                }
                task.resume()
            } catch let error as NSError {
                print("json serialization failed with error: \(error)")
            }
        }
    }
    public func isProductPurchased(_ productIdentifier: ProductIdentifier) -> Bool {
        return purchasedProductIdentifiers.contains(productIdentifier)
    }
    
    public class func canMakePayments() -> Bool {
        return SKPaymentQueue.canMakePayments()
    }
    
    public func restorePurchases() {
        SKPaymentQueue.default().restoreCompletedTransactions()
    }
}

// MARK: - SKProductsRequestDelegate

extension IAPHelper: SKProductsRequestDelegate {
    
    public func productsRequest(_ request: SKProductsRequest, didReceive response: SKProductsResponse) {
        print("Loaded list of products...")
        let products = response.products
        productsRequestCompletionHandler?(true, products)
        clearRequestAndHandler()
        for p in products {
            print("Found product: \(p.productIdentifier) \(p.localizedTitle) \(p.price.floatValue)")
        }
    }
    
    public func request(_ request: SKRequest, didFailWithError error: Error) {
        print("Failed to load list of products.")
        print("Error: \(error.localizedDescription)")
        productsRequestCompletionHandler?(false, nil)
        clearRequestAndHandler()
    }
    
    private func clearRequestAndHandler() {
        productsRequest = nil
        productsRequestCompletionHandler = nil
    }
}

// MARK: - SKPaymentTransactionObserver

extension IAPHelper: SKPaymentTransactionObserver {
    
    public func paymentQueue(_ queue: SKPaymentQueue, updatedTransactions transactions: [SKPaymentTransaction]) {
        for transaction in transactions {
            switch (transaction.transactionState) {
            case .purchased:
                complete(transaction: transaction)
                break
            case .failed:
                fail(transaction: transaction)
                break
            case .restored:
                restore(transaction: transaction)
                break
            case .deferred:
                break
            case .purchasing:
                break
            }
        }
    }
    
    
    func callForTransactionUpdate()
    {
        print("Reciept Data \(recieptData)")
        
        let params: [String: Any] = [
            "email": UserDefaults.standard.value(forKey:EMIALID)!,
            "password": UserDefaults.standard.value(forKey:USER_PASSWORD)!,
            "app_id": APPIDVALUE,
            "receipt_data": recieptData
        ]
        
        print("parameters -- \(params)")
        
        Alamofire.request("\(BASE_URL)\(UPDATE_SUBSCRIPTION_USER)", method: .post, parameters: params, encoding: JSONEncoding.default, headers: nil).responseObject { (response:DataResponse<User>)  in
            if Connectivity.isConnectedToInternet() {
                if response.result.value != nil
                {
                    let json = response.result.value!
                    print("JSON : \(json)")
                    switch json.code{
                    case "10"?:
                        print(json)
                        let resultDic = json.resultArray?.object(at: 0) as! NSDictionary
                        debugPrint("resultDic: \(resultDic)")
                        let accessToken = resultDic["accessToken"] as! String
                        let defaults = UserDefaults.standard
                        defaults.set(accessToken, forKey: "accessToken")
                        //                    let custID = resultDic["customer_id"] as! String
                        //                    defaults.set(custID, forKey:CUSTOMER_ID)
                        let profilePic = resultDic["profile_pic"] as! String
                        defaults.set(profilePic, forKey:PROFILE_PIC)
                        //                    let SubscriptionID = resultDic["subscription_id"] as! String
                        //                    defaults.set(SubscriptionID, forKey:SUBSCRIPTION_ID)
                        let userName =  String(format:"%@ %@",resultDic["first_name"] as! String,resultDic["last_name"] as! CVarArg)
                        defaults.set(userName, forKey:UERS_NAME_COMMENT)
                        defaults.set(true, forKey: APP_LAUNCH)
                        defaults.set("1", forKey:IS_FOLLOWER)
                        break
                    case "0"?:
                        // completion(Result.failure((json.msg)!))
                        break
                    case "1"?:
                        // completion(Result.failure((json.msg)!))
                        break
                    case "500"?:
                        //  completion(Result.failure((json.msg)!))
                        break
                    case "17"?:
                        // completion(Result.failure((json.msg)!))
                        break
                    case .none:
                        // completion(Result.failure((json.msg)!))
                        break
                    case .some(_):
                        // completion(Result.failure((json.msg)!))
                        break
                    }
                }
                else
                {
                }
            }
            else{
            }
        }
    }
    
    func callForTransaction()
    {
        
        let user_info = UserDefaults.standard.value(forKey:"cardDetailsInfo") as! NSMutableDictionary
        print("dict parameters \(user_info)")
        print("Reciept Data \(recieptData)")
        let str = UserDefaults.standard.value(forKey: FCM_TOKEN)
        print("TOKEN : \(str!)")
        let params: [String: Any] = [
            "token": "asdasdfdsfdsfdsfgdsg",
            "amount": "50",
            "first_name": user_info[FIRSTNAME]!,
            "last_name": user_info[LASTNAME]!,
            "name": user_info[NAME]!,
            "email": user_info[EMAIL]!,
            "password": user_info[PASSWORD]!,
            "plan_id": "3112",
            "app_id": user_info[APPID]!,
            "device_token": str!,
            "device_type": "ios",
            "receipt_data": recieptData
        ]
        
        print("parameters -- \(params)")
        
        Alamofire.request("\(BASE_URL)\(REGISTRATION_API)", method: .post, parameters: params, encoding: JSONEncoding.default, headers: nil).responseObject { (response:DataResponse<User>)  in
            if Connectivity.isConnectedToInternet() {
                if response.result.value != nil
                {
                    let json = response.result.value!
                    print("JSON : \(json)")
                    switch json.code{
                    case "10"?:
                        print(json)
                        let resultDic = json.resultArray?.object(at: 0) as! NSDictionary
                        debugPrint("resultDic: \(resultDic)")
                        let accessToken = resultDic["accessToken"] as! String
                        let defaults = UserDefaults.standard
                        defaults.set(accessToken, forKey: "accessToken")
                        //                    let custID = resultDic["customer_id"] as! String
                        //                    defaults.set(custID, forKey:CUSTOMER_ID)
                        let profilePic = resultDic["profile_pic"] as! String
                        defaults.set(profilePic, forKey:PROFILE_PIC)
                        //                    let SubscriptionID = resultDic["subscription_id"] as! String
                        //                    defaults.set(SubscriptionID, forKey:SUBSCRIPTION_ID)
                        let userName =  String(format:"%@ %@",resultDic["first_name"] as! String,resultDic["last_name"] as! CVarArg)
                        defaults.set(userName, forKey:UERS_NAME_COMMENT)
                        defaults.set(true, forKey: APP_LAUNCH)
                        defaults.set("1", forKey:IS_FOLLOWER)
                        UserDefaults.standard.set(true, forKey: USERLOGGEDIN)
                        break
                    case "0"?:
                        // completion(Result.failure((json.msg)!))
                        break
                    case "1"?:
                        // completion(Result.failure((json.msg)!))
                        break
                    case "500"?:
                        //  completion(Result.failure((json.msg)!))
                        break
                    case "17"?:
                        // completion(Result.failure((json.msg)!))
                        break
                    case .none:
                        // completion(Result.failure((json.msg)!))
                        break
                    case .some(_):
                        // completion(Result.failure((json.msg)!))
                        break
                    }
                }
                else
                {
                }
            }
            else{
            }
        }
    }
    
    private func complete(transaction: SKPaymentTransaction) {
        print("complete...")
        receiptValidation()
        if UserDefaults.standard.value(forKey: UPDATE_SUBSCRIPTION_USER) != nil &&
            UserDefaults.standard.value(forKey: UPDATE_SUBSCRIPTION_USER) as! Bool == true{
            self.callForTransactionUpdate()
        }
        else{
            self.callForTransaction()
        }
        deliverPurchaseNotificationFor(identifier: transaction.payment.productIdentifier)
        SKPaymentQueue.default().finishTransaction(transaction)
    }
    
    private func restore(transaction: SKPaymentTransaction) {
        guard let productIdentifier = transaction.original?.payment.productIdentifier else { return }
        print("restore... \(productIdentifier)")
        deliverPurchaseNotificationFor(identifier: productIdentifier)
        SKPaymentQueue.default().finishTransaction(transaction)
    }
    
    private func fail(transaction: SKPaymentTransaction) {
        print("fail...")
        if let transactionError = transaction.error as NSError?,
            let localizedDescription = transaction.error?.localizedDescription,
            transactionError.code != SKError.paymentCancelled.rawValue {
            print("Transaction Error: \(localizedDescription)")
            NotificationCenter.default.post(name: .IAPHelperPurchaseNotification, object: localizedDescription)
            
        }
        
        SKPaymentQueue.default().finishTransaction(transaction)
        
        SVProgressHUD.dismiss()
    }
    
    private func deliverPurchaseNotificationFor(identifier: String?) {
        guard let identifier = identifier else { return }
        purchasedProductIdentifiers.insert(identifier)
        UserDefaults.standard.set(true, forKey: identifier)
        NotificationCenter.default.post(name: .IAPHelperPurchaseNotification, object: identifier)
    }
}
