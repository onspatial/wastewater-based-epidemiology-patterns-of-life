# Wastewater Based Epidemiology Patterns of Life

This repository contains code and supporting resources for analyzing wastewater based epidemiology (WBE) data using patterns of life simulation. The goal is to extract population level signals from wastewater measurements and relate them to temporal and spatial human mobility patterns.

Interactive output of the simulation can be accessed on [https://poop-simcity.pages.dev/](https://poop-simcity.pages.dev/) 

***Amiri, Hossein, et al. "Where do We Poop? City-Wide Simulation of Defecation Behavior for Wastewater-Based Epidemiology." arXiv preprint arXiv:2601.04231 (2026)***

## Overview

Wastewater based epidemiology provides a non invasive way to monitor public health at scale. By combining wastewater signals with patterns of life modeling, this project aims to improve interpretation of trends, anomalies, and behavioral effects in communities.

## Repository Structure

```
├── src/ Source code
├── run/ Scripts for running experiments
├── results/ Illustrative output data
├── parameters.properties Configuration file
├── Pipfile Python dependencies
├── mvn.sh Maven helper script
└── README.md Project documentation
```

## Installation

Clone the repository:
```
git clone https://github.com/onspatial/wastewater-based-epidemiology-patterns-of-life.git
cd wastewater-based-epidemiology-patterns-of-life
```
Install Java dependencies:
```
bash mvn.sh
```
Install Python dependencies:
```
pipenv install
```

Run the project:
```
sh run/run.sh
```
## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit changes
4. Open a pull request

## License

Apache 2.0 License

## Disclaimer

This software is for research purposes only and should not be used as the sole basis for public health decisions.

## Configurable Population and Behavior Controls

The following table summarizes the configurable controls used to generate the synthetic population. Parameter names are described conceptually rather than by implementation variable name.

| Parameter             | Configurable control                                                                                  | Current value or range                                       |
| --------------------- | ----------------------------------------------------------------------------------------------------- | ------------------------------------------------------------ |
| Population size       | Number of agents in the simulated population                                                          | 10,000 in this study                                         |
| Simulation time       | Duration and temporal resolution of the simulation                                                    | 5-minute steps                                               |
| Age distribution      | Minimum age and additional age range assigned to agents                                               | 18 to 60 years                                               |
| Education composition | Proportions assigned to low education, high school or college, bachelor's, and graduate groups        | 10%, 54%, 23%, 13%                                           |
| Household structure   | Relative frequency of single agents and families with children                                        | 335 singles and 298 families with children per 1,000 agents  |
| Housing supply        | Number of apartments generated per 1,000 agents                                                       | 1,500 per 1,000 agents                                       |
| Workplace supply      | Number of workplaces generated per 1,000 agents                                                       | 250 per 1,000 agents                                         |
| Restaurant supply     | Number of restaurants generated per 1,000 agents                                                      | 20 per 1,000 agents                                          |
| Recreation supply     | Number of social venues generated per 1,000 agents                                                    | 10 per 1,000 agents                                          |
| Housing affordability | Maximum rent-to-salary ratio used when assigning housing                                              | 0.3333                                                       |
| Housing cost          | Baseline rent and room-count range                                                                    | 500 base rent; 1 to 4 rooms                                  |
| Work behavior         | Daily work duration                                                                                   | 8 hours                                                      |
| Income distribution   | Hourly pay range and initial financial balance range                                                  | 100 to 1,000 hourly rate; additional balance of 500 to 1,000 |
| Food behavior         | Appetite range and minimum meal duration at restaurants                                               | 0.2 to 0.8; 20 minutes                                       |
| Defecation behavior   | Individual defecation-rate range                                                                      | 0.2 to 0.8                                                   |
| Mobility behavior     | Walking speed and minimum and maximum site-visit duration                                             | 1.4 m/s; 20 to 180 minutes                                   |
| Social tendency       | Number of interests and target number of friends                                                      | 10 interests; up to 40 friends                               |
| Social network        | Probability of forming new ties through focal and cyclic closure                                      | 0.0025 and 0.01                                              |
| Social-tie dynamics   | Initial tie strength, decay, strengthening, and deletion threshold                                    | 0.01, 0.7, 0.04, 0.001                                       |
| Social need           | Loneliness threshold and social-status update parameters                                              | 7 days; increase of 0.07 and decrease of 0.03                |
| Venue choice          | Weight of distance, age similarity, income similarity, and interest similarity in social-venue choice | 1.0, 1.0, 1.0, 1.2                                           |
| Disease seeding       | Initial infected population and baseline transmission rate                                            | 1 initially infected; 0.1 baseline rate                      |
| Disease progression   | Exposure, infectious, and recovery durations                                                          | 3, 3, and 3 days by default                                  |
| Transmission capacity | Maximum number of infection attempts per person and per location                                      | 1 and 1 by default                                           |

Because these controls can be adjusted independently, the same simulation framework can be reparameterized for another city or institution without changing the model structure. In this study, we use one reproducible configuration to evaluate the effects of mobility and defecation behavior on wastewater signals. Full census-level calibration of Fulton County is left for future work.
