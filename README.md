# Wastewater Based Epidemiology Patterns of Life

This repository contains code and supporting resources for analyzing wastewater based epidemiology (WBE) data using patterns of life simulation. The goal is to extract population level signals from wastewater measurements and relate them to temporal and spatial human mobility patterns.

## Overview

Wastewater based epidemiology provides a non invasive way to monitor public health at scale. By combining wastewater signals with patterns of life modeling, this project aims to improve interpretation of trends, anomalies, and behavioral effects in communities.

## Repository Structure

.
├── src/ Source code
├── run/ Scripts for running experiments
├── results/ Illustrative output data
├── parameters.properties Configuration file
├── Pipfile Python dependencies
├── mvn.sh Maven helper script
└── README.md Project documentation

## Installation

Clone the repository:

git clone https://github.com/onspatial/wastewater-based-epidemiology-patterns-of-life.git
cd wastewater-based-epidemiology-patterns-of-life

Install dependencies:

bash mvn.sh

pipenv install

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit changes
4. Open a pull request

## License

Apache 2.0 License

## Disclaimer

This software is for research purposes only and should not be used as the sole basis for public health decisions.
