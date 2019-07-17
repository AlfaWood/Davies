//
//  Connectivity.swift
//  Alpha
//
//  Created by Monika Tiwari on 09/04/18.
//  Copyright © 2018 Stpl. All rights reserved.
//

import Foundation
import Alamofire

class Connectivity {
    class func isConnectedToInternet() ->Bool {
        return NetworkReachabilityManager()!.isReachable
    }
}
