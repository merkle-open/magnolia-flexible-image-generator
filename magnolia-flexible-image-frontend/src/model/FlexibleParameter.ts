import { Asset } from '../factory/ImageModelFactory.ts';

export type Parameter = {
	[name: string]: string;
};

export interface DynamicImageParameter {
	crop: boolean;
	toMap: () => Parameter;
}
export function DefaultDynamicImageParameterToMap(param: DynamicImageParameter): Parameter {
	return {
		crop: param.crop.toString(),
	};
}

export interface FlexibleParameter extends Asset {
	version: string;
	ratio: string;
	width: number;
	dynamicImageParameter?: DynamicImageParameter;
	toMap: () => Parameter;
}
export function DefaultFlexibleParameterToMap(param: FlexibleParameter): Parameter {
	return {
		...param.dynamicImageParameter?.toMap(),
		width: param.width.toString(),
		ratio: param.ratio,
		version: param.version,
	};
}
