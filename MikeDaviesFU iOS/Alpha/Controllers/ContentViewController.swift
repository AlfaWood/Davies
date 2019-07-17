//
//  ContentViewController.swift
//  Alpha
//
//  Created by Razan Nasir on 26/04/18.
//  Copyright © 2018 Stpl. All rights reserved.
//

import UIKit
import MediaPlayer
import AVFoundation
import AVKit
import SVProgressHUD
import Alamofire
import AlamofireObjectMapper
import ObjectMapper
import WebKit

class ContentViewController: UIViewController {
    var MyObservationContext = 0
    var observing = false
    //OUTLETS
    @IBOutlet weak var scrollView: UIScrollView!
    @IBOutlet var webViewHeightConstrain: NSLayoutConstraint!
    @IBOutlet var webView: UIWebView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var imageView: UIImageView!
    @IBOutlet weak var videoImageView: UIImageView!
    @IBOutlet weak var playVideoUpImg: UIImageView!
    @IBOutlet weak var imgPlayVideo: UIImageView!
    //VARIABLES
    var resourceID : Int?
    var resourceTopic : String?
    var resourceSubTopicID: Int?
    var contentDictionary :NSDictionary!
    var contentAsset :NSDictionary!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        let tapGestureRecognizerOnImage = UITapGestureRecognizer(target: self, action: #selector(ContentViewController.imageTapped(_:)))
        imageView.isUserInteractionEnabled = true
        imageView.addGestureRecognizer(tapGestureRecognizerOnImage)
        let tapGestureRecognizerOnVideo = UITapGestureRecognizer(target: self, action: #selector(ContentViewController.videoImageTapped(_:)))
        videoImageView.isUserInteractionEnabled = true
        videoImageView.addGestureRecognizer(tapGestureRecognizerOnVideo)
        titleLabel.sizeToFit()
        self.tabBarController?.tabBar.isHidden = true
        webView.scrollView.isScrollEnabled = false
        webView.delegate = self
    }
    
    override func viewWillAppear(_ animated: Bool) {
        /*
         let navView = UIView()
         navView.frame =  CGRect(x:-350, y:-10, width:950, height:65)
         let image = UIImageView()
         image.image = UIImage(named: "DPE-Inline")
         image.frame = CGRect(x:-350, y:-10, width:950, height:65)
         navView.sizeToFit()
         // Add both the label and image view to the navView
         navView.addSubview(image)
         // Set the navigation bar's navigation item's titleView to the navView
         self.navigationItem.titleView = navView
         // Set the navView's frame to fit within the titleView
         navView.sizeToFit()
         */
        
        updateNavigationBarUI()
        
        imgPlayVideo.isHidden = true
        playVideoUpImg.isHidden = true
        imageView.isHidden = true
        videoImageView.isHidden = true
        
        showContentData()
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
    
    @objc func imageTapped(_ sender:UITapGestureRecognizer){
        if contentAsset != nil {
            let asset_type:String?
            let videoUrl:String?
            let imageUrl:String?
            if contentAsset![ASSET_TYPE] as? String != nil{
                asset_type = contentAsset![ASSET_TYPE] as? String
            }else{
                asset_type = ""
            }
            if contentAsset![VIDEO_URL] as? String != nil{
                videoUrl = contentAsset![VIDEO_URL] as? String
            }else{
                videoUrl = ""
            }
            if contentAsset![IMAGE] as? String != nil {
                imageUrl = contentAsset![IMAGE] as? String
            }else{
                imageUrl = ""
            }
            if asset_type == "video"{
                if  videoUrl != "" {
                    let videoURL = URL(string: videoUrl!)
                    let player = AVPlayer(url: videoURL!)
                    let playerViewController = AVPlayerViewController()
                    playerViewController.player = player
                    self.present(playerViewController, animated: true) {
                        playerViewController.player!.play()
                    }
                }
            }
            else if asset_type == "image"{
                if  imageUrl != "" {
                    let vc=self.storyboard?.instantiateViewController(withIdentifier: "ImageViewController") as! ImageViewController
                    vc.checkValue = "content"
                    vc.imageUrl = imageUrl
                    self.navigationController?.pushViewController(vc, animated: true)
                }
            }
            else if asset_type == "both"{
                if  imageUrl != "" {
                    let vc=self.storyboard?.instantiateViewController(withIdentifier: "ImageViewController") as! ImageViewController
                    vc.checkValue = "content"
                    vc.imageUrl = imageUrl
                    self.navigationController?.pushViewController(vc, animated: true)
                }
            }
        }
    }
    
    @objc func videoImageTapped(_ sender:UITapGestureRecognizer){
        let videoUrl: String?
        if contentAsset![VIDEO_URL] as? String != nil{
            videoUrl = contentAsset![VIDEO_URL] as? String
        }else{
            videoUrl = ""
        }
        if  videoUrl != "" {
            let videoURL = URL(string: videoUrl!)
            let player = AVPlayer(url: videoURL!)
            let playerViewController = AVPlayerViewController()
            playerViewController.player = player
            self.present(playerViewController, animated: true) {
                playerViewController.player!.play()
            }
        }
    }
    
    func showContentData(){
        if contentAsset != nil{
            if contentAsset[TITLE] as? String != nil{
                titleLabel.text = String(format : "%@",contentAsset[TITLE] as! String)
            }
            if contentAsset[DESCRIPTION] as? String != nil{
                UITextView.appearance().linkTextAttributes = [NSAttributedStringKey.foregroundColor.rawValue: UIColor.blue]
                var descString = contentAsset[DESCRIPTION] as? String
                if (descString?.contains("tel\">"))!{
                    var myStringArr = descString?.components(separatedBy: "tel\">")
                    print("Desc string --->>-\(String(describing: myStringArr![1]))")
                    var myStringArrNum = myStringArr![1].components(separatedBy: "<")
                    print("Desc string Number --->>-\(String(describing: myStringArrNum[0]))")
                    descString = descString?.replacingOccurrences(of: "href=\"tel\"", with:String(format: "href=tel:%@", myStringArrNum[0] as CVarArg))
                }
                if (descString?.contains("email\">"))!{
                    var myStringArrEmailStr = descString?.components(separatedBy: "email\">")
                    print("Desc string --->>-\(String(describing: myStringArrEmailStr![1]))")
                    var myStringArrEmail = myStringArrEmailStr![1].components(separatedBy: "<")
                    print("Desc string Email --->>-\(String(describing: myStringArrEmail[0]))")
                    descString = descString?.replacingOccurrences(of: "href=\"email\"", with:String(format: "href=mailto:%@", myStringArrEmail[0] as CVarArg))
                }
                if (descString?.contains("href=\"url\">"))!{
                    var myStringArrWebSiteStr = descString?.components(separatedBy: "href=\"url\">")
                    print("Desc string --->>-\(String(describing: myStringArrWebSiteStr![1]))")
                    var myStringArrWebsite = myStringArrWebSiteStr![1].components(separatedBy: "<")
                    print("Desc string Website --->>-\(String(describing: myStringArrWebsite[0]))")
                    descString = descString?.replacingOccurrences(of: "href=\"url\"", with:String(format: "href=http://%@", myStringArrWebsite[0] as CVarArg))
                }
                print("Desc string --->>-\(String(describing: descString))")
                descString = descString?.replacingOccurrences(of: "width=", with: " ")
                descString = descString?.replacingOccurrences(of: "height=", with: " ")
                descString = descString?.replacingOccurrences(of: "<iframe", with: "<p align='center'><iframe")
                guard (descString?.data(using: String.Encoding.unicode)) != nil else { return }
                
                webView.mediaPlaybackRequiresUserAction = false
                webView.loadHTMLString(descString!,
                                       baseURL: nil)
            }
            let asset_type : String?
            let imageUrl : String?
            let ThumbUrl : String?
            if contentAsset![ASSET_TYPE] as? String != nil{
                asset_type = contentAsset![ASSET_TYPE] as? String
            }else{
                asset_type = ""
            }
            if contentAsset![IMAGE] as? String != nil{
                imageUrl = contentAsset![IMAGE] as? String
            }else{
                imageUrl = ""
            }
            if contentAsset![THUMBNAIL_URL] as? String != nil{
                ThumbUrl = contentAsset![THUMBNAIL_URL] as? String
            }else{
                ThumbUrl = ""
            }
            if asset_type == "video"{
                videoImageView.isHidden = true
                imageView.isHidden = false
                if  ThumbUrl != nil{
                    imageView.loadImageUsingCache(withUrl: ThumbUrl!)
                    imgPlayVideo.isHidden = true
                    playVideoUpImg.isHidden = false
                }else{
                    imgPlayVideo.isHidden = true
                    playVideoUpImg.isHidden = true
                    imageView.image = UIImage(named: "noimage")
                }
            }
            else if asset_type == "image"{
                imageView.isHidden = false
                videoImageView.isHidden = true
                if  imageUrl != "" {
                    imgPlayVideo.isHidden = true
                    playVideoUpImg.isHidden = true
                    imageView.loadImageUsingCache(withUrl: imageUrl!)
                }else{
                    imgPlayVideo.isHidden = true
                    playVideoUpImg.isHidden = true
                    imageView.image = UIImage(named: "noimage")
                }
            }
            else  if asset_type == "both"{
                imageView.isHidden = false
                videoImageView.isHidden = false
                if  imageUrl != nil && ThumbUrl != nil{
                    imageView.loadImageUsingCache(withUrl: imageUrl!)
                    videoImageView.loadImageUsingCache(withUrl: ThumbUrl!)
                    imgPlayVideo.isHidden = false
                    playVideoUpImg.isHidden = true
                    
                }else{
                    imgPlayVideo.isHidden = true
                    playVideoUpImg.isHidden = true
                    imageView.image = UIImage(named: "noimage")
                    videoImageView.image = UIImage(named: "noimage")
                }
            }
            else{
                imageView.isHidden = true
                videoImageView.isHidden = true
                imgPlayVideo.isHidden = true
                playVideoUpImg.isHidden = true
            }
        }
        else{
            imgPlayVideo.isHidden = true
            playVideoUpImg.isHidden = true
            imageView.isHidden = true
            videoImageView.isHidden = true
        }
    }
    
    private func webView(webView: UIWebView!, didFailLoadWithError error: NSError!) {
        print("Webview fail with error \(error)");
    }
    
    private func webViewDidStartLoad(webView: UIWebView!) {
        print("Webview started Loading")
    }
    private func webViewDidFinishLoad(webView: UIWebView!) {
        print("Webview did finish load")
    }
    
    func adjustUITextViewHeight(arg : UITextView){
        arg.sizeToFit()
        arg.isScrollEnabled = false
    }
    
    func UTCToLocal(date:String) -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yy-MM-dd HH:mm:ss"
        dateFormatter.timeZone = TimeZone(abbreviation: "UTC")
        let dt = dateFormatter.date(from: date)
        dateFormatter.timeZone = TimeZone.current
        dateFormatter.dateFormat = "dd/M/yyyy @ h:mm a"
        return dateFormatter.string(from: dt!)
    }
    
    func startObservingHeight() {
        let options = NSKeyValueObservingOptions([.new])
        webView.scrollView.addObserver(self, forKeyPath: "contentSize", options: options, context: &MyObservationContext)
        observing = true;
    }
    
    func stopObservingHeight() {
        webView.scrollView.removeObserver(self, forKeyPath: "contentSize", context: &MyObservationContext)
        observing = false
    }
    
    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        guard let keyPath = keyPath else {
            super.observeValue(forKeyPath: nil, of: object, change: change, context: context)
            return
        }
        switch keyPath {
        case "contentSize":
            if context == &MyObservationContext {
                webViewHeightConstrain.constant = webView.scrollView.contentSize.height
            }
        default:
            super.observeValue(forKeyPath: keyPath, of: object, change: change, context: context)
        }
    }
}
extension ContentViewController : UIWebViewDelegate {
    
    func webView(_ webView: UIWebView, shouldStartLoadWith request: URLRequest, navigationType: UIWebViewNavigationType) -> Bool {
        switch navigationType {
        case .linkClicked:
            print(request)
            guard let url = request.url else { return true }
            if #available(iOS 10.0, *) {
                print(url)
                UIApplication.shared.open(url, options: [:], completionHandler: nil)
            } else {
                print(url)
                UIApplication.shared.openURL(url)
            }
            return false
        default:
            return true
        }
    }
    
    func webViewDidFinishLoad(_ webView: UIWebView) {
        webViewHeightConstrain.constant = webView.scrollView.contentSize.height
        if (!observing) {
            startObservingHeight()
        }
    }
}


