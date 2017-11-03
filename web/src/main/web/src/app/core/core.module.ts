import {NgModule, Optional, SkipSelf} from "@angular/core";

import {SettingsService} from "./settings/settings.service";
import {ThemesService} from "./themes/themes.service";
import {TranslatorService} from "./translator/translator.service";
import {MenuService} from "./menu/menu.service";

import {throwIfAlreadyLoaded} from "./module-import-guard";
import {RequestsService} from "./requests/requests.service";
import {AuthService} from "./auth/auth.service";
import {AuthGuard} from "./auth/auth.guard";
import {HttpClientModule} from "@angular/common/http";
import {ComponentsModule} from "../components/components.module";
import {ContractsService} from "./contracts/contracts.service";
import {UserService} from "./user/user.service";
import {RequestService} from "../services/request/request.service";

@NgModule({
  imports: [
    HttpClientModule,
    ComponentsModule
  ],
  providers: [
    SettingsService,
    ThemesService,
    TranslatorService,
    MenuService,
    RequestsService,
    RequestService,
    ContractsService,
    AuthService,
    AuthGuard,
    UserService
  ],
  declarations: [],
  exports: []
})

export class CoreModule {
  constructor(@Optional() @SkipSelf() parentModule: CoreModule) {
    throwIfAlreadyLoaded(parentModule, 'CoreModule');
  }
}
