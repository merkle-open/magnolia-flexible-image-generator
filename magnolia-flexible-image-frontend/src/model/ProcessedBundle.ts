export interface ProcessedBundle {
	name: string;
	imageSizes: ImageSize[];
	customRenditions: ImageSize[];
}

export interface ImageSize {
	ratio?: string;
	width: number;
	id: string;
}
