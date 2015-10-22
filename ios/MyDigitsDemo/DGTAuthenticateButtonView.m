//
//  DGTAuthenticateButtonView.m
//  MyDigitsDemo
//
//  Created by JP Driver on 10/22/15.
//  Copyright © 2015 Facebook. All rights reserved.
//

#import "DGTAuthenticateButtonView.h"
#import <DigitsKit/DigitsKit.h>

@implementation DGTAuthenticateButtonView

RCT_EXPORT_MODULE()

-(UIView*) view {

  DGTAuthenticateButton *authButton;
  authButton = [DGTAuthenticateButton buttonWithAuthenticationCompletion:^(DGTSession *session, NSError *error) {
    if (session.userID) {
      // TODO: associate the session userID with your user model
      NSString *msg = [NSString stringWithFormat:@"Phone number: %@", session.phoneNumber];
      UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"You are logged in!"
                                                      message:msg
                                                     delegate:nil
                                            cancelButtonTitle:@"OK"
                                            otherButtonTitles:nil];
      [alert show];
    } else if (error) {
      NSLog(@"Authentication error: %@", error.localizedDescription);
    }
  }];

  return authButton;

}


@end
