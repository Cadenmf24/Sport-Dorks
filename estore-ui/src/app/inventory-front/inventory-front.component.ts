import { Component, OnInit } from '@angular/core';

import { Item } from '../item';
import { InventoryService } from '../inventory.service';
import { MessageService } from '../message.service';
import { Route, RouterOutlet } from '@angular/router';
import { Router } from '@angular/router';

@Component({
  selector: 'app-inventory-front',
  templateUrl: './inventory-front.component.html',
  styleUrls: ['./inventory-front.component.css']
})
export class InventoryFrontComponent implements OnInit {

  // items = ITEMS;


  selectedItem?: Item;
  json = require('../items.json')
  items: Item[] = [];

  constructor(
    private router:Router,
    private inventoryService: InventoryService, 
    private messageService: MessageService) { }



  ngOnInit(): void {
    this.getItems();
  }

  getItems(): void {
    this.inventoryService.getInventory().subscribe(items => this.items = items);
  }

  searchItems(): void {

  let name = prompt("Search Inventory");
  
  
  if (name != null){
    
  let x = this.inventoryService.searchInventory(name).subscribe(items => this.items = items)

  }

  else {
    this.inventoryService.getInventory().subscribe(items => this.items = items)
  }

  }

  goToPage(pageName:string):void{
    this.router.navigate([`${pageName}`]);
  }

}
