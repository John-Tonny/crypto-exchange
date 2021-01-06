//
//  ChargeMoneyViewController.m
//  digitalCurrency
//
//  Created by iDog on 2018/2/7.
//  Copyright © 2018年 XinHuoKeJi. All rights reserved.
//

#import "ChargeJlqViewController.h"
#import "MineNetManager.h"
#import "WalletManageModel.h"

@interface ChargeJlqViewController ()

@property (weak, nonatomic) IBOutlet UITextField *shopAccount;
@property (weak, nonatomic) IBOutlet UITextField *amountText;
@property (weak, nonatomic) IBOutlet UITextField *shopPassword;
@property (weak, nonatomic) IBOutlet UIButton *eyeButton;
- (IBAction)openEyeAction:(id)sender;
@property (weak, nonatomic) IBOutlet UIButton *chargeButton;
- (IBAction)chargeAction:(id)sender;

@property (weak, nonatomic) IBOutlet UILabel *contentlabel;
@property (weak, nonatomic) IBOutlet NSLayoutConstraint *topheight;
@property(nonatomic,strong)WalletManageModel *clickModel;

@end

@implementation ChargeJlqViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.unit = [self.unit uppercaseString];
    self.title = [NSString stringWithFormat:@"%@",[[ChangeLanguage bundle] localizedStringForKey:@"chargeMoney" value:nil table:@"English"]];
    
    self.shopAccount.placeholder = [[ChangeLanguage bundle] localizedStringForKey:@"inputShopAccount" value:nil table:@"English"];
    self.amountText.placeholder = [[ChangeLanguage bundle] localizedStringForKey:@"inputJlqAmount" value:nil table:@"English"];
    self.shopPassword.placeholder = [[ChangeLanguage bundle] localizedStringForKey:@"inputShopPassword" value:nil table:@"English"];

    [_shopAccount setValue:RGBOF(0x999999) forKeyPath:@"_placeholderLabel.textColor"];
    [_amountText setValue:RGBOF(0x999999) forKeyPath:@"_placeholderLabel.textColor"];
    [_shopPassword setValue:RGBOF(0x999999) forKeyPath:@"_placeholderLabel.textColor"];

    if ([self.address isEqualToString:@""] || self.address == nil) {
        
        [self getassetwalletresetaddress];

    }else{
        self.shopAccount.text = self.address;
    }
   
    if (kWindowW == 320 ) {
        self.topheight.constant =20;

    }
   
    
}

//获取提币地址
-(void)getassetwalletresetaddress{

    [EasyShowLodingView showLodingText:LocalizationKey(@"loading")];
    [MineNetManager getassetwalletresetaddress:@{@"unit":self.unit} CompleteHandle:^(id resPonseObj, int code) {
        [EasyShowLodingView hidenLoding];
        if (code) {
            if ([resPonseObj[@"code"] integerValue] == 0) {
                [self getData];
            }else{
                [self.view makeToast:resPonseObj[MESSAGE] duration:1.5 position:CSToastPositionCenter];
            }
        }else{
            [self.view makeToast:LocalizationKey(@"noNetworkStatus") duration:1.5 position:CSToastPositionCenter];
        }
    }];
    
}

//MARK:---获取我的钱包所有数据
-(void)getData{
    [MineNetManager getMyWalletInfoForCompleteHandle:^(id resPonseObj, int code) {
        
        if (code) {
            if ([resPonseObj[@"code"] integerValue] == 0) {
            NSArray *dataArr = [WalletManageModel mj_objectArrayWithKeyValuesArray:resPonseObj[@"data"]];
             
                for (WalletManageModel *model in dataArr) {
                    if ([model.coin.unit isEqualToString:self.unit]) {
                        self.address = model.address;
                    }
                }
                
                if ([self.address isEqualToString:@""] || self.address == nil) {
                    [self.view makeToast:LocalizationKey(@"unChargeJlqTip1") duration:1.5 position:CSToastPositionCenter];
                }
                
            }else{
                [self.view makeToast:resPonseObj[MESSAGE] duration:1.5 position:CSToastPositionCenter];
            }
        }else{
            [self.view makeToast:LocalizationKey(@"noNetworkStatus") duration:1.5 position:CSToastPositionCenter];
        }
    }];
}



-(void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
//    [self setNavigationControllerStyle];

}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

- (IBAction)openEyeAction:(id)sender {
    self.eyeButton.selected = !self.eyeButton.selected;
    self.shopPassword.secureTextEntry = !self.eyeButton.selected;
}
- (IBAction)chargeAction:(id)sender {
    if ([self.shopAccount.text isEqualToString:@""]) {
        [self.view makeToast:[[ChangeLanguage bundle] localizedStringForKey:@"unChargeJlqTip1" value:nil table:@"English"] duration:1.5 position:CSToastPositionCenter];
        return;
    }
    if ([self.amountText.text isEqualToString:@""]) {
        [self.view makeToast:[[ChangeLanguage bundle] localizedStringForKey:@"inputJlqAmount" value:nil table:@"English"] duration:1.5 position:CSToastPositionCenter];
        return;
    }
    if ([self.shopPassword.text isEqualToString:@""]){
        [self.view makeToast:[[ChangeLanguage bundle] localizedStringForKey:@"inputShopPassword" value:nil table:@"English"] duration:1.5 position:CSToastPositionCenter];
        return;
    }
    [EasyShowLodingView showLodingText:[[ChangeLanguage bundle] localizedStringForKey:@"loading" value:nil table:@"English"]];
    [MineNetManager chargeCoinApplyForUnit:self.unit withAddress:self.shopAccount.text withAmount:self.amountText.text withShopPassword:self.shopPassword.text CompleteHandle:^(id resPonseObj, int code){
        [EasyShowLodingView hidenLoding];
        if (code) {
            if ([resPonseObj[@"code"] integerValue] == 0) {
                [self.view makeToast:resPonseObj[MESSAGE] duration:1.5 position:CSToastPositionCenter];
                dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
                    [self.navigationController popViewControllerAnimated:YES];
                });
            }else if ([resPonseObj[@"code"] integerValue] == 3000 ||[resPonseObj[@"code"] integerValue] == 4000 ){
                //[ShowLoGinVC showLoginVc:self withTipMessage:resPonseObj[MESSAGE]];
                [YLUserInfo logout];
            }else{
                [self.view makeToast:resPonseObj[MESSAGE] duration:1.5 position:CSToastPositionCenter];
            }
        }else{
            [self.view makeToast:[[ChangeLanguage bundle] localizedStringForKey:@"noNetworkStatus" value:nil table:@"English"] duration:1.5 position:CSToastPositionCenter];
        }
    }];
}
@end
