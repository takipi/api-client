package com.takipi.api.client.functions.input;

import com.takipi.api.client.util.validation.ValidationUtil.VolumeType;
import com.takipi.integrations.functions.annotations.Function;
import com.takipi.integrations.functions.annotations.Param;
import com.takipi.integrations.functions.annotations.Function.FunctionType;
import com.takipi.integrations.functions.annotations.Param.ParamType;


@Function(name="volume", type=FunctionType.Variable,
description = " This function returns a single stat value depicting the avg/sum/unique count of a target event\n" + 
		" set's volume", 
example="volume({\"type\":\"count\",\"volumeType\":\"hits\",\"eventVolumeType\":\"hits\",\"view\":\"$view\",\n" + 
		"\"timeFilter\":\"$timeFilter\",\"environments\":\"$environments\", \"applications\":\"$applications\",\n" + 
		"\"servers\":\"$servers\",\"deployments\":\"$deployments\",\"types\":\"$type\",\n" + 
		"\"transactions\":\"$transactions\",\"filter\":\"events\", \n" + 
		"\"pointsWanted\":\"$pointsWanted\"})", 
image="https://drive.google.com/file/d/1GXyStXf4yFRn4mKRRpJLNMcdXknwdM80/view?usp=sharing", isInternal=false)
public class VolumeInput extends BaseVolumeInput {
	

	@Param(type=ParamType.Enum, advanced=false, 
			literals={VOLUME_TYPE_HITS, VOLUME_TYPE_INVOCATIONS, 
					VOLUME_TYPE_ALL},
			defaultValue=VOLUME_TYPE_ALL,
			description = "The volume type aggregated by this function:\n" +
					VOLUME_TYPE_HITS + ": aggregate volume for all event that had hit volume\n" + 
					VOLUME_TYPE_INVOCATIONS +": aggregate the ratio between even volume and the calls into events locations\n" + 
					VOLUME_TYPE_ALL +":  aggregate volume for all events with either hits or invocations available\n")
	public VolumeType volumeType;
}
