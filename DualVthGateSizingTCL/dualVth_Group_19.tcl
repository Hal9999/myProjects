proc getCellsFullName { } {
    set cells {}
    foreach_in_collection cell [get_cells] {
        lappend cells [get_attribute $cell full_name]
    }
    return $cells
}

proc getCellRefName { cellFullName } {
    return [get_attribute $cellFullName ref_name]
}

proc getClock { } {
	redirect -variable timingReport {report_clock}
	
    if { [regexp {clk\s+([0-9.]+)} $timingReport match clkPeriod] } {
    	return [scan $clkPeriod %f]
    } else { error "Should not be here: report_clock bad formatted, can't find clock period!\n$timingReport" }
}

proc leakPower { } {
    array set magnitudes [list  m 1000000000\
                                 u 1000000   \
                                 n 1000      \
                                 p 1         ]
    redirect -variable powerReport {report_power}
     
	if { [regexp -line {^Cell Leakage Power\s+=\s+([0-9\.]+)\s+([munp])?W$} $powerReport match value unit] } {       
		return [expr [scan $value %f] * $magnitudes($unit)]
    } else { error "Should not be here: report_power bad formatted and regexp failed!\n$cellPower" }
}

proc parseTiming { clockPeriod constraint {ord "list"} } {
    redirect -variable timingReport {report_timing}
    
    if { [regexp {slack \(([^\)]+)\)\s+([0-9.-]+)} $timingReport match condition value] } {
        set slack [expr $constraint - ($clockPeriod - [scan $value %f])]
        
    	if { $slack == 0 && $condition eq "VIOLATED: increase signficant digits" } {
            set met 0
        } else {
            set met [expr $slack >= 0]
        }
    } else { error  "Should not be here: report_timing bad formatted!\n$timingReport" }
    
    if { $ord eq "boolean" } { return $met }
    
    set worstPathCells [regexp -all -line -inline {U[0-9]+} $timingReport]
    
    return [list $worstPathCells $slack $met]
}

proc getCellAlternativesLeakDelta { cellFullName } {
	array set libs 	[list \
						_LL_  {CORE65LPLVT_nom_1.00V_25C.db:CORE65LPLVT/} \
						_LH_  {CORE65LPHVT_nom_1.00V_25C.db:CORE65LPHVT/} \
						_LLS_ {CORE65LPLVT_nom_1.00V_25C.db:CORE65LPLVT/} \
						_LHS_ {CORE65LPHVT_nom_1.00V_25C.db:CORE65LPHVT/} \
					]
    set cellRefName [getCellRefName $cellFullName]
	set alternatives [split [get_object_name [get_alternative_lib_cells $cellFullName] ] " "]
	
	set preLeak [leakPower]
	
	lappend deltas [list $cellRefName 0]
    foreach alt $alternatives {
        set destRefName [lindex [split $alt "/"] end]
        regexp _LL_|_LLS_|_LH_|_LHS_ $destRefName destType
		size_cell $cellFullName "$libs($destType)$destRefName"
        
        set deltaLeak [expr [leakPower] - $preLeak]
		
        lappend deltas [list $destRefName $deltaLeak]
    }
	size_cell $cellFullName "$libs([regexp -inline _LL_|_LLS_|_LH_|_LHS_ $cellRefName])$cellRefName"
	
    return $deltas
}

proc getCellAlternativesSurrogatedDelta { cellFullName } {
	array set typePenalties 	[list \
									_LHS_ 0 \
									_LH_  0 \
									_LL_  1000 \
									_LLS_ 1000 \
								]
    set cellRefName [getCellRefName $cellFullName]
	set alternatives [split [get_object_name [get_alternative_lib_cells $cellFullName] ] " "]
	
	regexp _LL_|_LLS_|_LH_|_LHS_ $cellRefName destType
	lappend deltas [list $cellRefName $typePenalties($destType)]
    foreach alt $alternatives {
        set destRefName [lindex [split $alt "/"] end]

        regexp _LL_|_LLS_|_LH_|_LHS_ $destRefName destType
		set dimension [scan [lindex [split $destRefName "X"] end] %f]
        set pseudoCost [expr $dimension + $typePenalties($destType) ]
		
        lappend deltas [list $destRefName $pseudoCost]
    }
	
    return $deltas
}

proc getCellAlternativesLeakRelativeDelta { cellFullName { data "real" } } {
	set alternatives [list]
    
	if { $data eq "real" } {
		set alternatives [getCellAlternativesLeakDelta $cellFullName]
	} else {
		set alternatives [getCellAlternativesSurrogatedDelta $cellFullName]
	}
	
	set alternatives [lsort -decreasing -real -index 1 [lsort -decreasing -index 0 $alternatives]]

    for {set i 0} {$i < [expr [llength $alternatives] -1]} {incr i} {
        lset alternatives $i 1 [expr [lindex $alternatives $i 1] - [lindex $alternatives [expr $i +1] 1]]
    }
    lset alternatives [expr [llength $alternatives] -1] 1 "x"
    return $alternatives
}

proc lllsearch { lll search } {
	set llli 0
	foreach ll $lll {
		set lli 0
		foreach l $ll {
			set li 0
			foreach e $l {
				if { $e eq $search } { return [list $llli $lli $li] }
				incr li
			}
			incr lli
		}
		incr llli
	}
	return -1
}

proc highEffort { constraint } {
    set refsCache [list] ;
	array set libs 	[list \
						_LL_  {CORE65LPLVT_nom_1.00V_25C.db:CORE65LPLVT/} \
						_LH_  {CORE65LPHVT_nom_1.00V_25C.db:CORE65LPHVT/} \
						_LLS_ {CORE65LPLVT_nom_1.00V_25C.db:CORE65LPLVT/} \
						_LHS_ {CORE65LPHVT_nom_1.00V_25C.db:CORE65LPHVT/} \
					]
	
	array set cellsBase {}
    array set indexesBase {}
    
	set iterD 0
    foreach cellFullName [getCellsFullName] {
        set cellRefName [getCellRefName $cellFullName]
		
		set indexes [lllsearch $refsCache $cellRefName]
        if { $indexes == -1 } {
            set alternatives [getCellAlternativesLeakRelativeDelta $cellFullName "pseudo"]
			lappend refsCache $alternatives
			set indexes [lllsearch $refsCache $cellRefName]
        }
        array set cellsBase [list $cellFullName [lindex $refsCache [lindex $indexes 0] ] ]
		
		set lastRefName [lindex $refsCache [lindex $indexes 0] end 0]
		regexp _LL_|_LLS_|_LH_|_LHS_ $lastRefName type
		if { $cellRefName ne $lastRefName } { size_cell $cellFullName "$libs($type)$lastRefName" }
        set index [expr [llength [lindex $refsCache [lindex $indexes 0] ] ] -1 ]
		array set indexesBase [list $cellFullName $index]
    }
	
	set iterC 0
    set clockPeriod [getClock]
    while 1 {
        set timing [parseTiming $clockPeriod $constraint]
        if { [lindex $timing 2] } { break }

        while 1 {
            set possibilities {}
            foreach cellFullName [lindex $timing 0] {
                if { [set index $indexesBase($cellFullName)] > 0 } {
                    incr index -1
                    lappend possibilities [list $cellFullName [lindex $cellsBase($cellFullName) $index 1] $index]
                }
            }
            if { [llength $possibilities] > 0 } {
                set possibilities [lsort -real -index 1 $possibilities]
                set chosenCellFullName [lindex $possibilities 0 0]
                set index [lindex $possibilities 0 2]
				set destRefName [lindex $cellsBase($chosenCellFullName) $index 0]
				size_cell $chosenCellFullName "$libs([regexp -inline _LL_|_LLS_|_LH_|_LHS_ $destRefName])$destRefName"
                if { [lindex $timing 1] > [lindex [parseTiming $clockPeriod $constraint] 1] } {
					set destRefName [lindex $cellsBase($chosenCellFullName) [expr $index + 1] 0]
					size_cell $chosenCellFullName "$libs([regexp -inline _LL_|_LLS_|_LH_|_LHS_ $destRefName])$destRefName"
					if { $index == 0 } {
						set cellsBase($chosenCellFullName) [lreplace $cellsBase($chosenCellFullName) 0 0]
					} else {
						set cost [expr [lindex $cellsBase($chosenCellFullName) $index 1] + [lindex $cellsBase($chosenCellFullName) [expr $index - 1] 1] ]
	                    lset cellsBase($chosenCellFullName) [expr $index -1] 1 $cost
                   		set cellsBase($chosenCellFullName) [lreplace $cellsBase($chosenCellFullName) $index $index]
					}
                    incr indexesBase($chosenCellFullName) -1
                    continue
                } else {
                    incr indexesBase($chosenCellFullName) -1
                    break
                }
            } else {
                puts "Non è possibile migliorare ancora il path critico: requisito temporale non soddisfacibile."
                return
            }
        }
    }
}

proc lowEffort { arrivalTime } {
	array set libs 	[list \
						_LL_  {CORE65LPLVT_nom_1.00V_25C.db:CORE65LPLVT/} \
						_LH_  {CORE65LPHVT_nom_1.00V_25C.db:CORE65LPHVT/} \
						_LLS_ {CORE65LPLVT_nom_1.00V_25C.db:CORE65LPLVT/} \
						_LHS_ {CORE65LPHVT_nom_1.00V_25C.db:CORE65LPHVT/} \
					]
    set clockPeriod [getClock]
	if { [parseTiming $clockPeriod $arrivalTime "boolean"] == 0 } { error "low effort does not support arrivalTime < clock, try with highEffort" }

    set cells [getCellsFullName]
	
    foreach cellFullName $cells {
        set preRefName [getCellRefName $cellFullName]
        
        set destRef [regsub "_LL_" $preRefName "_LH_"]
        set destRef [regsub "_LLS_" $destRef "_LHS_"]
        if { $preRefName eq $destRef } { continue }
		size_cell $cellFullName "$libs([regexp -inline _LL_|_LLS_|_LH_|_LHS_ $destRef])$destRef"
        
        if { [parseTiming $clockPeriod $arrivalTime "boolean"] == 0 } {
            size_cell $cellFullName "$libs([regexp -inline _LL_|_LLS_|_LH_|_LHS_ $preRefName])$preRefName"
        }
    }
}

proc getCombinationalArea { } {
	set chip_area 0.0
	foreach_in_collection point_cell [get_cells] {
		if { [get_attribute $point_cell is_combinational ] == true } {
			set chip_area [expr $chip_area + [get_attribute $point_cell area]]
		}
	}
	return $chip_area
}

proc leakage_opt { effort targetTiming } {
    set initialTotalLeak [leakPower]
	set initialCombinationalArea [getCombinationalArea]
    set initTime [clock clicks -milliseconds]
    
    #----------------------------------------------------------------------------------------------
    switch $effort {
        "low" {
            lowEffort $targetTiming
        }
        "high" {
            highEffort $targetTiming
        }
        default { error "Unsupported effort($effort): supported are <low> and <high>" }
	}
    #----------------------------------------------------------------------------------------------
    
    set endTime [clock clicks -milliseconds]
    set finalCombinationalArea [getCombinationalArea]	
    set finalTotalLeak [leakPower]
	set finalCells [getCellsFullName]
	set totalCells [llength $finalCells]
	set lvtCount 0
	set hvtCount 0
	foreach cell $finalCells {
		if { [regexp {_((LH)|(LHS))_} [getCellRefName $cell]] } {
			incr hvtCount
		} else { incr lvtCount }
	}
    
	set res [list]
	lappend res [expr double([expr $initialTotalLeak - $finalTotalLeak]*100)/$initialTotalLeak]
	lappend res [expr double($endTime - $initTime)/1000]
	lappend res [expr double($lvtCount*100)/$totalCells]
	lappend res [expr double($hvtCount*100)/$totalCells]
	lappend res $finalCombinationalArea
	
	puts "Celle        totali    #  = $totalCells"
	puts "                lvt    #  = $lvtCount"
	puts "                hvt    #  = $hvtCount"
	puts "Area       iniziale  um2  = $initialCombinationalArea"
	puts "             finale  um2  = $finalCombinationalArea"
	puts "Leak       iniziale   pW  = $initialTotalLeak pW"
	puts "             finale   pW  = $finalTotalLeak pW"
    puts "        risparmiato   pW  = [expr $initialTotalLeak - $finalTotalLeak] pW"
	puts "                       %  = [expr double([expr $initialTotalLeak - $finalTotalLeak]*100)/$initialTotalLeak] %"
	puts "Tempo                  s  = [expr double($endTime - $initTime)/1000]"
	
	return $res
}
