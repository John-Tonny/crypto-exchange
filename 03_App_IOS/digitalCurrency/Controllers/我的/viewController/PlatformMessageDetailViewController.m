//
//  PlatformMessageDetailViewController.m
//  digitalCurrency
////  Created by iDog on 2018/3/21.
//  Copyright © 2018年 XinHuoKeJi. All rights reserved.
//

#import "PlatformMessageDetailViewController.h"
#import "MineNetManager.h"

@interface PlatformMessageDetailViewController ()<UIWebViewDelegate>
@property(nonatomic,strong)UIWebView *webView;
@end

@implementation PlatformMessageDetailViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.navigationItem.title = self.navtitle;
//    self.title = LocalizationKey(@"notice");
    [self getNoticeDetail];  //john
    //[self.view addSubview:[self webView]]; //john
    // Do any additional setup after loading the view.
}

-(UIWebView *)webView{
    if (!_webView) {
        _webView = [[UIWebView alloc] initWithFrame:CGRectMake(0, 0, kWindowW, kWindowH-NEW_NavHeight)];
        _webView.delegate = self;
        [_webView scalesPageToFit];
        [_webView loadHTMLString:self.content baseURL:nil];
        _webView.opaque = NO;
        _webView.backgroundColor = mainColor;
    }
    return _webView;
}

-(void)webViewDidStartLoad:(UIWebView *)webView{
   
}

-(void)webViewDidFinishLoad:(UIWebView *)webView{
    [EasyShowLodingView hidenLoding];
    NSString *js=@"var script = document.createElement('script');"
    "script.type = 'text/javascript';"
    "script.text = \"function ResizeImages() { "
    "var myimg,oldwidth;"
    "var maxwidth = %f;"
    "for(i=0;i <document.images.length;i++){"
    "myimg = document.images[i];"
    "if(myimg.width > maxwidth){"
    "oldwidth = myimg.width;"
    "myimg.width = %f;"
    "}"
    "}"
    "}\";"
    "document.getElementsByTagName('head')[0].appendChild(script);";
    js=[NSString stringWithFormat:js,[UIScreen mainScreen].bounds.size.width,[UIScreen mainScreen].bounds.size.width-20];
    [webView stringByEvaluatingJavaScriptFromString:js];
    [webView stringByEvaluatingJavaScriptFromString:@"ResizeImages();"];
    [webView stringByEvaluatingJavaScriptFromString:@"document.getElementsByTagName('body')[0].style.webkitTextFillColor= '#E6E6E6'"];
}

-(void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error{
    [EasyShowLodingView hidenLoding];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

// john
-(void)getNoticeDetail{
    [EasyShowLodingView showLodingText:LocalizationKey(@"loading")];
    [MineNetManager getPlatformMessageDetailForCompleteHandle:self.id withLang:@"CN" CompleteHandle:^(id resPonseObj, int code) {
        [EasyShowLodingView hidenLoding];
        NSLog(@"resPonseObj -- %@",resPonseObj);
        
        if (code) {
            if ([resPonseObj[@"code"] integerValue] == 0) {
                self.content = resPonseObj[@"data"][@"info"][@"content"];
                [self.view addSubview:[self webView]];
            }else{
                [self.view makeToast:resPonseObj[MESSAGE] duration:1.5 position:CSToastPositionCenter];
            }
        }else{
            [self.view makeToast:LocalizationKey(@"noNetworkStatus") duration:1.5 position:CSToastPositionCenter];
        }
    }];
    
}
@end
