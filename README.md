# International Research Experiences for Students (IRES): U.S.-Mexico Interdisciplinary Research Collaboration for Smart Cities Safe Community System (SCS) application prototype.

## Description
This repository forms part of the SCS application prototype. The purpose of this application is to collect real-time data from user-generated reports of safety-related events.

## Repository Content
- Security-mobile-app: SCS Android Mobile App prototype.
- Reports-server: Restful API for storing reports.
- Map-server: Server for displaying the map as a webpage.

## Instructions for creating the MongoDB database
1. Create a database:<br />
    - use ires2019<br />
2. Create collections:<br />
    - db.createCollection('alerts')<br />
    - db.createCollection('allReports')<br />
    - db.createCollection('securityUsers')<br />

## Instructions for running the servers
1. Reports Server: <br />
    - Enter to the following path:<br />
      Reports-server/<br />
    - and run:<br />
      node index.js<br />
2. Map Server<br />
    - Enter to the following path:<br />
      Map-server/src/<br />
    - and run:<br />
      node index.js<br />

## Participating Institutions
+ University of Texas at El Paso (UTEP)
+ Universidad de Guadalajara (UdeG), Smart Cities Innovation Center

## IRES investigators
+ Dr. Ruey (Kelvin) Cheu - UTEP
+ Dr. Victor M. Larios Rosillo - UdeG
+ Dr. Oscar A. Mondragon Campos - UTEP
+ Dr. Natalia Villanueva Rosales - UTEP

## IRES contributing participants
+ Adriana C. Camacho
+ Ubaldo Castro
+ Carlos Chavez
+ Jonatan Contreras
+ Edgar Escobedo
+ Itzel E. Rivas

## Acknowledgements
This material is based upon work supported by the National Science Foundation (NSF) Grant No. 1658733 IRES: U.S.-Mexico Interdisciplinary Research Collaboration for Smart Cities. This work used resources from Cyber-ShARE Center of Excellence, which is supported by NSF Grant No. HRD-1242122. Any opinions, findings, and conclusions or recommendations expressed in this material are those of the author(s) and do not necessarily reflect the views of the NSF.

## Copyright
&#169; 2018-2021, IRES: U.S.-Mexico Interdisciplinary Research Collaboration for Smart Cities investigators and contributing participants.

## Credits
+ Mobile app icons provided by Font Awesome by Dave Gandy - http://fontawesome.io licensed under SIL OFL 1.1   
+ Map Data by © OpenStreetMap Contributors under license Open Data Commons Open Database License (ODbL) - https://www.openstreetmap.org/
+ Leaflet libraries by Vladimir Agafonkin. Maps © OpenStreetMap contributors - https://leafletjs.com/

## Licenses
Data is Licensed under Attribution-ShareAlike 4.0 International (CC BY-SA 4.0) 

Sourde code is Licensed under the GNU General Public License v3.0, you may not use this file except in compliance with the License.
This program comes with ABSOLUTELY NO WARRANTY. This is free software, and you are welcome to redistribute it under certain conditions.
