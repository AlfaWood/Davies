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


class ProductCell: UITableViewCell {
    
    @IBOutlet var buttonSubscribe: UIButton!
    @IBOutlet var titleText: UILabel!
    @IBOutlet var detailText: UILabel!
    @IBOutlet var subDetailText: UILabel!
    
    static let priceFormatter: NumberFormatter = {
        let formatter = NumberFormatter()
        
        formatter.formatterBehavior = .behavior10_4
        formatter.numberStyle = .currency
        
        return formatter
    }()
    
    var buyButtonHandler: ((_ product: SKProduct) -> Void)?
    
    var product: SKProduct? {
        didSet {
            guard let product = product else { return }
            
            subDetailText?.text = product.localizedTitle
            
            if RazeFaceProducts.store.isProductPurchased(product.productIdentifier)
            {
                //        accessoryType = .checkmark
                accessoryView = nil
                let priceStr = ProductCell.priceFormatter.string(from: product.price) ?? "Not available"
                titleText.text = "Monthly"
                detailText.text = priceStr
                buttonSubscribe.addTarget(self, action: #selector(ProductCell.buyButtonTapped(_:)), for: .touchUpInside)
                
                //        detailText.text = ""
            }
            else if IAPHelper.canMakePayments()
            {
                let priceStr = ProductCell.priceFormatter.string(from: product.price) ?? "Not available"
                titleText?.text = "Monthly"
                detailText.text = priceStr
                buttonSubscribe.addTarget(self, action: #selector(ProductCell.buyButtonTapped(_:)), for: .touchUpInside)
                //        accessoryType = .none
                //        accessoryView = self.newBuyButton()
            }
            else
            {
                detailText?.text = "Not available"
            }
        }
    }
    
    override func prepareForReuse() {
        super.prepareForReuse()
        
        subDetailText?.text = ""
        detailText.text = ""
        titleText?.text = ""
        accessoryView = nil
    }
    
    //  func newBuyButton() -> UIButton {
    ////    let button = UIButton(type: .system)
    ////    buttonSubscribe.setTitleColor(tintColor, for: .normal)
    ////    button.setTitle("Subscribe", for: .normal)
    ////    button.backgroundColor = UIColor.red
    //
    //    return buttonSubscribe
    //  }
    
    @objc func buyButtonTapped(_ sender: AnyObject) {
        SVProgressHUD.show()
        buyButtonHandler?(product!)
    }
}
