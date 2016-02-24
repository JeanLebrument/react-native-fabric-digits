#import <DigitsKit/DigitsKit.h>
#import "RCTDigitsManager.h"

@implementation RCTDigitsManager

RCT_EXPORT_MODULE()

- (unsigned int)intFromHexString:(NSString *)hexStr {
    unsigned int hexInt = 0;
    
    // Create scanner
    NSScanner *scanner = [NSScanner scannerWithString:hexStr];
    
    // Tell scanner to skip the # character
    [scanner setCharactersToBeSkipped:[NSCharacterSet characterSetWithCharactersInString:@"#"]];
    
    // Scan hex value
    [scanner scanHexInt:&hexInt];
    
    return hexInt;
}

- (UIColor *)getUIColorObjectFromHexString:(NSString *)hexStr alpha:(CGFloat)alpha
{
    // Convert hex string to an integer
    unsigned int hexint = [self intFromHexString:hexStr];
    
    // Create color object, specifying alpha as well
    UIColor *color =
    [UIColor colorWithRed:((CGFloat) ((hexint & 0xFF0000) >> 16))/255
                    green:((CGFloat) ((hexint & 0xFF00) >> 8))/255
                     blue:((CGFloat) (hexint & 0xFF))/255
                    alpha:alpha];
    
    return color;
}

- (void)setColorToApparence:(DGTAppearance *)appearance
           withKeyApparence:(NSString *)keyAppearance
                fromOptions:(NSDictionary *)options
                 withKeyHex:(NSString *)keyHex
                andKeyAlpha:(NSString *)keyAlpha {
    id hex = [options valueForKeyPath:keyHex];
    id alpha = [options valueForKeyPath:keyAlpha];
    
    if (hex && alpha && [hex isKindOfClass:[NSString class]] && [alpha isKindOfClass:[NSNumber class]]) {
        [appearance setValue:[self getUIColorObjectFromHexString: (NSString *)hex alpha:[(NSNumber *)alpha doubleValue]] forKey:keyAppearance];
    }
}

- (void)setFontToApparence:(DGTAppearance *)appearance
          withKeyApparence:(NSString *)keyAppearance
               fromOptions:(NSDictionary *)options
               withKeyName:(NSString *)keyName
                andKeySize:(NSString *)keySize {
    id fontName = [options valueForKeyPath:keyName];
    id fontSize = [options valueForKeyPath:keySize];
    
    if (fontName && [fontName isKindOfClass:[NSString class]] && [fontSize isKindOfClass:[NSNumber class]]) {
        [appearance setValue:[UIFont fontWithName:(NSString *)fontName size:[(NSNumber *)fontSize doubleValue]] forKey:keyAppearance];
    }
}

- (void)setImageToApparence:(DGTAppearance *)appearance
           withKeyApparence:(NSString *)keyAppearance
                fromOptions:(NSDictionary *)options
           withKeyImageName:(NSString *)keyImageName {
    id imageName = [options valueForKeyPath:keyImageName];
    
    if (imageName && imageName && [imageName isKindOfClass:[NSString class]]) {
        [appearance setValue:[UIImage imageNamed:imageName] forKey:keyAppearance];
    }
}

RCT_REMAP_METHOD(launchAuthentication,
                 view:(NSDictionary*)options
                 resolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject) {
    
    DGTAppearance *appearance = [[DGTAppearance alloc] init];
    DGTAuthenticationConfiguration *configuration = [[DGTAuthenticationConfiguration alloc] initWithAccountFields:DGTAccountFieldsDefaultOptionMask];
    
    [self setColorToApparence:appearance withKeyApparence:@"backgroundColor" fromOptions:options withKeyHex:@"appearance.backgroundColor.hex" andKeyAlpha:@"appearance.backgroundColor.alpha"];
    [self setColorToApparence:appearance withKeyApparence:@"accentColor" fromOptions:options withKeyHex:@"appearance.accentColor.hex" andKeyAlpha:@"appearance.accentColor.alpha"];
    [self setFontToApparence:appearance withKeyApparence:@"headerFont" fromOptions:options withKeyName:@"appearance.headerFont.name" andKeySize:@"appearance.headerFont.size"];
    [self setFontToApparence:appearance withKeyApparence:@"labelFont" fromOptions:options withKeyName:@"appearance.labelFont.name" andKeySize:@"appearance.labelFont.size"];
    [self setFontToApparence:appearance withKeyApparence:@"bodyFont" fromOptions:options withKeyName:@"appearance.bodyFont.name" andKeySize:@"appearance.bodyFont.size"];
    [self setImageToApparence:appearance withKeyApparence:@"logoImage" fromOptions:options withKeyImageName:@"appearance.logoImageName"];
    
    configuration.title = options[@"title"];
    configuration.phoneNumber = options[@"phoneNumber"];
    configuration.appearance = appearance;
    
    dispatch_async(dispatch_get_main_queue(), ^{
        UIViewController *root = [[[[UIApplication sharedApplication] delegate] window] rootViewController];
        
        [[Digits sharedInstance] authenticateWithViewController:root configuration:configuration completion:^(DGTSession *session, NSError *error) {
            if (error) {
                reject([NSString stringWithFormat: @"%lu", (long)error.code], error.localizedDescription, error);
            } else {
                Digits *digits = [Digits sharedInstance];
                DGTOAuthSigning *oauthSigning = [[DGTOAuthSigning alloc] initWithAuthConfig:digits.authConfig authSession:session];
                NSDictionary *authHeaders = [oauthSigning OAuthEchoHeadersToVerifyCredentials];
                resolve(authHeaders);
            }
        }];
    });
}

RCT_EXPORT_METHOD(logout) {
    [[Digits sharedInstance] logOut];
}

@end