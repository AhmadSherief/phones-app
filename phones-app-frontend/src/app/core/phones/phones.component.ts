import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { PhonesService } from '../services/phones.service';

export interface PhoneData {
  country: string;
  valid: boolean;
  code: string;
  number: string;
}

@Component({
  selector: 'app-phones',
  templateUrl: './phones.component.html',
  styleUrls: ['./phones.component.scss'],
})
export class PhonesComponent implements OnInit, AfterViewInit {
  displayedColumns: string[] = ['country', 'state', 'code', 'number'];
  dataSource: MatTableDataSource<PhoneData>;
  countries: any;
  params: any = {
    page: 0,
    pageSize: 5,
  };
  state: String = 'All';
  code: String = 'All';
  totalCount: number = 0;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  constructor(private phoneService: PhonesService) {
    this.dataSource = new MatTableDataSource();
    this.phoneService.getCountries().then((res) => {
      this.countries = res;
    });
  }

  ngOnInit(): void {
    this.updatePhones();
  }
  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  updatePhones() {
    this.phoneService.getPhones(this.params).then((res: any) => {
      this.dataSource.data = res['phones'];
      this.totalCount = res['totalCount'];
      setTimeout(() => {
        this.paginator.pageIndex = this.params.page;
        this.paginator.length = this.totalCount;
      });
    });
  }

  updatePage(event: any) {
    this.params.page = event.pageIndex;
    this.params.pageSize = event.pageSize;
    this.updatePhones();
  }
  updateFilter() {
    if (this.state == 'All') {
      delete this.params.state;
    } else {
      this.params.state = this.state;
    }

    if (this.code == 'All') {
      delete this.params.code;
    } else {
      this.params.code = this.code;
    }
    this.params.page = 0;
    this.updatePhones();
  }
}
