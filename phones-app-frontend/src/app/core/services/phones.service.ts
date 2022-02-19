import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class PhonesService {
  public baseURL = environment.BaseURL;

  constructor(private httpClient: HttpClient) {}

  public getPhones(params: any) {
    return this.httpClient
      .get(this.baseURL + `/api/jumia/v1/customers/phones`, {
        params,
      })
      .toPromise();
  }
  public getCountries() {
    return this.httpClient
      .get(this.baseURL + `/api/jumia/v1/countries`)
      .toPromise();
  }
}
