import { NgModuleRef, ApplicationRef } from '@angular/core';
import { createNewHosts } from '@angularclass/hmr';

// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
export const hmrBootstrap = (module: any, bootstrap: () => Promise<NgModuleRef<any>>) => {
// eslint-disable-next-line @typescript-eslint/no-explicit-any -- intentionally loose typing in a generic helper / framework edge case
  let ngModule: NgModuleRef<any>;
  module.hot.accept();
  bootstrap().then(mod => ngModule = mod);
  module.hot.dispose(() => {
    const appRef: ApplicationRef = ngModule.injector.get(ApplicationRef);
    const elements = appRef.components.map(c => c.location.nativeElement);
    const makeVisible = createNewHosts(elements);
    ngModule.destroy();
    makeVisible();
  });
};
