export interface ImageModel {
	alt: string;
	title: string;
	description: string;
	original: string;
	assetName: string;
	srcset: Rendition[];
	customRenditions: CustomRenditions;
	mimeType?: string;
}
export interface Rendition {
	media: string;
	src: string;
	width: number;
	height: number;
	ratio: number;
}
export type CustomRenditions = {
	[name: string]: string[];
};
