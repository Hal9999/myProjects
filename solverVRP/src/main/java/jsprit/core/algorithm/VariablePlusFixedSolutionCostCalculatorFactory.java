/*******************************************************************************
 * Copyright (c) 2014 Stefan Schroeder.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 3.0 of the License, or (at your option) any later version.
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     Stefan Schroeder - initial API and implementation
 ******************************************************************************/
package jsprit.core.algorithm;

import jsprit.core.algorithm.state.InternalStates;
import jsprit.core.analysis.SolutionAnalyser;
import jsprit.core.problem.Location;
import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.solution.SolutionCostCalculator;
import jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import jsprit.core.problem.solution.route.VehicleRoute;
import jsprit.core.problem.solution.route.state.RouteAndActivityStateGetter;
import jsprit.core.problem.vehicle.Vehicle;

/**
 * Default objective function which is the sum of all fixed vehicle and variable
 * transportation costs, i.e. each is generated solution is evaluated according
 * this objective function.
 * 
 * @author schroeder
 *
 */
public class VariablePlusFixedSolutionCostCalculatorFactory {
	
	private RouteAndActivityStateGetter stateManager;
	private int number;
	private VehicleRoutingProblem vrp;
	
	public VariablePlusFixedSolutionCostCalculatorFactory(RouteAndActivityStateGetter stateManager) {
		super();
		this.stateManager = stateManager;
	}
	
	public VariablePlusFixedSolutionCostCalculatorFactory(VehicleRoutingProblem vrp, RouteAndActivityStateGetter stateManager, int number) {
		super();
		this.stateManager = stateManager;
		this.number = number;
		this.vrp=vrp;
		//System.out.println("aaaaaaaaaaaaaaaaaaaa");
	}

	public SolutionCostCalculator createCalculator(){
		return new SolutionCostCalculator() {

			@Override
			public double getCosts(VehicleRoutingProblemSolution solution) {
				//System.out.println("bbbbbbbbbbbbbbbbbbbbbbb");
				solution.setManager(stateManager);
				String n = System.getProperty("number");
				int number = Integer.parseInt(n);
				double c = 0.0;
				int min=Integer.MAX_VALUE;
				int max =0;
				double media,somma=0;
				double sbilanciamento=0;
				double th;
                for(VehicleRoute r : solution.getRoutes()){
					c += stateManager.getRouteState(r, InternalStates.COSTS, Double.class);
					c += getFixedCosts(r.getVehicle());
					if(r.getActivities().size()<min) {
						min=r.getActivities().size();
					}
					if(r.getActivities().size()>max) {
						max = r.getActivities().size();
					}
					somma+=r.getActivities().size();
				}
                media = somma/solution.getRoutes().size();
                th=media*0.3;
                for(VehicleRoute r : solution.getRoutes()){
					if(r.getActivities().size()<th){
						sbilanciamento+=th-r.getActivities().size();
						sbilanciamento++;
					}
					if(r.getActivities().size()>(max-th)){
						sbilanciamento+=r.getActivities().size()-(max-th);
						sbilanciamento++;
					}
				}
                
                //sbilanciamento = Math.abs(media-max) + Math.abs(media-min);
                //sbilanciamento = Math.abs(max-min);
                c += solution.getUnassignedJobs().size() * c * .1;
                c += c*100*Math.abs(solution.getRoutes().size()- number)*Math.abs(solution.getRoutes().size()- number);
                c+=c*sbilanciamento*sbilanciamento*sbilanciamento*60;
				return c;
			}
			
			public double getCosts(VehicleRoutingProblemSolution solution, int numberOfVehicols) {
				System.out.println("ciaooooooo");
				double c = 0.0;
                for(VehicleRoute r : solution.getRoutes()){
					c += stateManager.getRouteState(r, InternalStates.COSTS, Double.class);
					c += getFixedCosts(r.getVehicle());
				}
                c += solution.getUnassignedJobs().size() * c * .1;
                c += c*10*Math.abs(solution.getRoutes().size()- numberOfVehicols);
				return c;
			}

            private double getFixedCosts(Vehicle vehicle) {
                if(vehicle == null) return 0.0;
                if(vehicle.getType() == null) return 0.0;
                return vehicle.getType().getVehicleCostParams().fix;
            }

			@Override
			public void setVrp(VehicleRoutingProblem vrp) {
				// TODO Auto-generated method stub
				
			}
		};
	}

}
