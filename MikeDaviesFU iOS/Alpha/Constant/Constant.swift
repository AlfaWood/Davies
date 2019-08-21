//
//  Constant.swift
//  Alpha
//
//  Created by Razan Nair on 29/03/18.
//  Copyright © 2018 Stpl. All rights reserved.
//

import Foundation
import UIKit

// VALIDATION ALERTS
let IN_APP_PRODUCT_ID = "MDFU"
let USER_NAME_ALERT = "Please enter username."
let VALID_USER_ALERT = "Username can have minimum 6 and maximum 20 characters. Special characters are not allowed."
let MESSAGE_BLANK_FIRST="First Name cannot be blank"
let MESSAGE_BLANK_LAST="Last Name cannot be blank"
let MESSAGE_BLANK_USER="Username cannot be blank"
let PASSWORD_ALERT = "Please enter password."
let OLD_PASSWORD_ALERT = "Please enter old password."
let NEW_PASSWORD_ALERT = "Please enter new password."
let PASSWORD_LENGTH_ALERT_LONG = "Password must be at maximum 20 characters."
let PASSWORD_LENGTH_ALERT = "Password can have minimum 6 and maximum 20 characters."
let CONFIRM_PASSWORD_ALERT = "Please re-enter password."
let PASSWORD_CONFIRM_PASSWORD_ALERT = "Your password and re-enter password do not match."
let QUESTION_BLANK_ALERT = "Please enter question."
let FIRSTNAME_ALERT = "Please enter first name."
let LASTNAME_ALERT = "Please enter last name."
let EMAIL_ALERT = "Please enter email address."
let VALID_EMAIL_ALERT = "Invalid email address."
let ADD_CARD_ALERT = "Do you want to change card? Please add a new card to replace with the existing card."
let ALERT = "Alert"
let ERROR = "Error"
let CLICKOK = "OK"
let MESSAGE = "message"
let URL_TERMS = "URL_Terms"
let NO_DATA = "No search result found."
let EMAIL_MESSAGE = "A user with this email already exists"
let NOINTERNET = "Internet connection not available."
let SOMESERVERISSUE = "Cannot reach server.Please try again later"
let EMAIL_VARIFICATIONT_TEXT = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}"
let CHARACTERSET = CharacterSet(charactersIn:
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")
let PASSWORD_LENGTH_ALERT_CHANGE = "Password must be at least 6 characters."
let DELETE_CARD_ALERT = "Are you sure you want to delete this card?"
let DELETE_CARD_ALERT_LASTCARD = "You must add another payment method first."
let CANCEL_CARD_ALERT = "Are you sure you want to cancel your membership?"

//let USERNAME_VERIFICATION = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_."

//VIEW CONTROLLERS ID
let REGISTRATION_VIEW_CONTROLLER_ONE = "RegistrationViewControllerOne"
let REGISTRATION_VIEW_CONTROLLER_TWO = "RegistrationViewControllerTwo"
let FEED_VIEW_CONTROLLER = "FeedViewController"
let LOGIN_VIEW_CONTROLLER = "LoginViewController"
let RESOURCE_VIEW_CONTROLLER = "ResourceViewController"
let QA_VIEW_CONTROLLER = "Q_AViewController"
let MESSAGE_VIEW_CONTROLLER = "MessagesViewController"
let TAB_BAR_VIEW_CONTROLLER = "TabBarViewController"
let DETAIL_FEED_VIEW_CONTROLLER = "DetailFeedViewController"
let BEFORE_FEED_VIEW_CONTROLLER = "BeforeFeedViewController"
let YT_PLAYER_VIEW_CONTROLLER  = "YTPlayerViewController"
let UPDATE_PASSWORD_VIEW_CONTROLLER  = "UpdatePasswordViewController"
let RESOURCE_SUBVIEW_VIEW_CONTROLLER  = "ResourceSubViewController"
let RESOURCE_SUBVIEW_VIEW_CONTROLLER_ONE  = "ResourceSubViewOne"
let RESOURCE_SUBVIEW_VIEW_CONTROLLER_TWO  = "ResourceSubViewTwo"
let CONTENT_VIEW_CONTROLLER  = "ContentViewController"
let SUBMITQUESTION_VIEW_CONTROLLER  = "SubmitQuestionViewController"
let SUBSCRIPTION_PLAN = "MasterViewController"
let TERMS_PLANVC = "TermsVC"
let COMNIG_FROM_NOTIFICATION = "IsComingFromNotification"
let USER_PROFILE_VIEW_CONTROLLER = "UserProfileView"
let NOTIF_DICT = "notifDict"
let APP_NAME = "Mike Davies Fitness Factory"
let ALPHA_NAME = "alpha_comment"
let cardDeleteMessage = "Card Deleted Successfully"
let HEX_COLOUR = "003087"
let tabTintColor = UIColor.init(red: 0/255.0, green: 48/255.0, blue: 135/255.0, alpha: 1.0)
let navTintColor = UIColor.white

//BASE URL
let BASE_URL = "https://www.iamanalpha1.com/api/"
//let BASE_URL = "https://1qa.iamanalpha1.com/api/"
//let BASE_URL = "https://iamanalpha1.com/api/"
//let BASE_URL = "http://iaaa.srmtechsol.com/api/"
//let BASE_URL = "http://54.85.241.222/api/"

// APPKEY API
let APPKEY_API = "appkey"

//REGISTRATION API
let REGISTRATION_API = "register"

// CARD ADD
let CARD_ADD_API = "cardAdd"

//LOGIN API
let LOGIN_API = "login"

//FEED API
let FEED_API = "getfeeds"

//GetCardList
let GET_CARD_List = "cardList"

//DeleteCard
let DELETE_CARD = "cardDelete"

//RESOURCE API
let RESOURCE_API = "getTopics"

//Like API
let LIKE_API = "likePost"

//GETRESOURCE API
let RESOURCE_CONTENT_API = "getContent"
let RESOURCE_SUBTOPIC_API = "getSubtopics"
let GET_QUESTION_ANSWER =  "getQA"

//RESET_PASSWORD API
let RESET_PASSWORD_API = "resetpassword"

//UPDATE_PASSWORD API
let UPDATE_PASSWORD_API = "updatepassword"

//Submit Question
let Insert_QA = "insertQA"

//CHECK_EMAIL API
let CHECK_EMAIL_API = "checkemail"

//Check UserName
let CHECK_USER_NAME = "checkUsername"

//updatePRofile
let UPDATE_PROFILE = "updateProfile"

//cancel Membership
let CANCEL_MEMBERSHIP = "cancelSubscription"

//GetProfile
let GET_PROFILE  = "getProfile"

//Get Plan List
let getPlanList = "planLists"

//GET MESSAGE
let GET_MESSAGE_API = "getMessages"
let GET_UNREAD_MESSAGE_API = "unreadMessages"
let UNREAD_MESSAGE = "unreadMessages"
let GET_READ_MESSAGE_API = "messageRead"
let GET_COMMENTS = "getComments"
let POST_COMMENTS = "postComment"
let SUCCESS = "SUCCESS"
let DATA = "data"
let STATUS = "status"
let MSG = "message"
let CODE = "code"
let RESULT = "result"
let RESULTMUT = "resultmut"
let ERRORSTR = "error"
let URL_EULA = "url"
let TOTAL_LIKES = "total_likes"
let COMMENT_COUNT = "comment_count"

// RESISTRATION PARAMS
let FIRSTNAME = "first_name"
let LASTNAME = "last_name"
let ACCESS_TOKEN = "accessToken"
let TOKEN = "token"
let PASSWORD_STATUS = "password_status"
let COMPLETED = "completed"
let PENDING = "pending"
let NAME = "name"
let EMAIL = "email"
let USER_NAME = "username"
let PASSWORD = "password"
let ANONYMOUS = "anonymous"
let QUESTION = "question"
let APP_ID = "app_id"
let OLD_PASSWORD = "old"
let NEW_PASSWORD = "new"
let RE_ENTERED_PASSWORD = "confirm"
let PAYMENT_NONCE = "payment_nonce"
let APPID = "app_id"
let DEVICE_TYPE = "device_type"
let APPIDVALUE = "MjAxOTA2MjQ1OTA1MjQ="
let TOKENIZATION_KEY = "tokenization_key"
let USERDATA = "userdata"
let USERLOGGEDIN = "userloggedin"
let EMIALID = "emailID"
let USER_PASSWORD = "user_password"
let CUSTOMER_ID = "customer_id"
let SUBSCRIPTION_ID = "subscription_id"
let PROFILE_PIC = "profile_pic"
let UERS_NAME_COMMENT = "user_name"
let IS_FOLLOWER = "follower"
let UPDATE_SUBSCRIPTION_USER = "updateSubscription"
let KEY = "key"
let APP_KEY = "app_key"
let APP_LAUNCH = "AppLaunchFirstTime"
let COMING_FROM_NOTIFICATION = "comingFromNotification"
let FCM_TOKEN = "Fcm_Token"
let TITLE = "title"
let DATE_TIME = "dateAndTime"
let FEED_TYPE = "socialPlatformName"
let STORYDATA = "storyData"
let STORYDATA_HTML = "storyData_with_html"
let FEED_IMAGE = "url"
let LIKE = "numberOfLikes"
let CURRENT_USER_LIKE_POST = "current_follower_likes_this_post"
let ALPHA_COMMENTED = "alpha_commented"
let FIRST_COMMENT = "first_comment"
let FIRST_COMMENT_NAME = "first_comment_name"
let SECOND_COMMENT = "second_comment"
let SECOND_COMMENT_NAME = "second_comment_name"
let MESSAGE_READ = "message_read"
let COMMENTS = "numberOfComments"
let ASSETS = "asset"
let ASSET_URL = "url"
let THUMBNAIL_URL = "thumbnail_url"
let ASSET_TYPE = "type"
let SOCIAL_PLATEFORM_NAME = "socialPlatformName"
let ASSET = "asset"
let ID = "id"
let SUB_ID = "sub_topic_id"
let SUB_TOPIC_ID = "sub_topic"
let VIDEO_URL = "video_url"
let TOPIC = "topic"
let IMAGE = "image"
let DESCRIPTION = "description"
let DESCRIPTION_TEXT = "description_text"
let AUTHORIZATION = "Authorization"
let CREATED_AT = "created_at"
let BEARER = "Bearer "
let MESSAGE_ID = "msg_id"
let FROM = "from"
let TO = "to"
let ATTACHMENT_URL = "attachment_url"
let FEED_ID = "feed_id"
let FEEd_Unique_ID = "feed_unique_id"
let PAGE_NO = "page"

extension UIViewController {
    func addAlertView(title: String, message: String, buttonTitle: String){
        let alert = UIAlertController(title: title, message: message, preferredStyle: UIAlertControllerStyle.alert)
        alert.addAction(UIAlertAction(title: buttonTitle, style: UIAlertActionStyle.default, handler: nil))
        present(alert, animated: true)
    }
}

// MARK: - CUSTOM UI COLOR
extension UIColor{
    convenience init(hexString: String) {
        let hex = hexString.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int = UInt32()
        Scanner(string: hex).scanHexInt32(&int)
        let a, r, g, b: UInt32
        switch hex.count {
        case 3: // RGB (12-bit)
            (a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
        case 6: // RGB (24-bit)
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8: // ARGB (32-bit)
            (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (255, 0, 0, 0)
        }
        self.init(red: CGFloat(r) / 255, green: CGFloat(g) / 255, blue: CGFloat(b) / 255, alpha: CGFloat(a) / 255)
    }

    convenience init(red: Int, green: Int, blue: Int) {
        assert(red >= 0 && red <= 255, "Invalid red component")
        assert(green >= 0 && green <= 255, "Invalid green component")
        assert(blue >= 0 && blue <= 255, "Invalid blue component")
        
        self.init(red: CGFloat(red) / 255.0, green: CGFloat(green) / 255.0, blue: CGFloat(blue) / 255.0, alpha: 1.0)
    }
    
    convenience init(rgb: Int) {
        self.init(
            red: (rgb >> 16) & 0xFF,
            green: (rgb >> 8) & 0xFF,
            blue: rgb & 0xFF
        )
    }
}




